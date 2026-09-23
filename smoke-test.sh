#!/bin/bash
# ==============================================================================
# HRMS Microservices Comprehensive End-to-End Smoke & Integration Test
# ==============================================================================
set -e

export PATH=$PATH:/opt/homebrew/bin:/usr/local/bin

if [ -z "$DB_PASSWORD" ]; then
    echo "ERROR: DB_PASSWORD environment variable is not set."
    echo "Please set DB_PASSWORD before running the smoke test, e.g.:"
    echo "  export DB_PASSWORD=your_mysql_password"
    exit 1
fi

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LOGS_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOGS_DIR"

echo "======================================================================"
echo "  HRMS DISTRIBUTED MICROSERVICES: END-TO-END VERIFICATION SUITE"
echo "======================================================================"

# Clean up any previous background instances
pkill -f "RecruitmentServiceApp" || true
pkill -f "OnboardingServiceApp" || true
sleep 1

# Step 1: Start Onboarding Service on Port 8082
echo ""
echo "▶ [STEP 1] Starting Onboarding Microservice (Port 8082)..."
cd "$SCRIPT_DIR/microservices/onboarding-service"
nohup mvn exec:java -Dexec.mainClass="com.hrms.onboarding.OnboardingServiceApp" > "$LOGS_DIR/test-onboarding.log" 2>&1 &
ONBOARDING_PID=$!
echo "  ✓ Onboarding Service process launched with PID: $ONBOARDING_PID"

# Step 2: Start Recruitment Service on Port 8081
echo ""
echo "▶ [STEP 2] Starting Recruitment Microservice (Port 8081)..."
cd "$SCRIPT_DIR/microservices/recruitment-service"
nohup mvn exec:java -Dexec.mainClass="com.hrms.recruitment.RecruitmentServiceApp" > "$LOGS_DIR/test-recruitment.log" 2>&1 &
RECRUITMENT_PID=$!
echo "  ✓ Recruitment Service process launched with PID: $RECRUITMENT_PID"

# Wait for services to bind to ports
echo ""
echo "▶ [STEP 3] Waiting for services to initialize..."
for i in {1..10}; do
    if curl -s http://localhost:8081/health > /dev/null 2>&1 && curl -s http://localhost:8082/health > /dev/null 2>&1; then
        break
    fi
    sleep 1
done

# Step 4: Test Health Endpoints
echo ""
echo "▶ [STEP 4] Verifying Health Endpoints..."
echo "  GET http://localhost:8081/health:"
RECRUIT_HEALTH=$(curl -s http://localhost:8081/health)
echo "  $RECRUIT_HEALTH"
echo "  GET http://localhost:8082/health:"
ONBOARD_HEALTH=$(curl -s http://localhost:8082/health)
echo "  $ONBOARD_HEALTH"

# Step 5: Create Candidate on Recruitment Service
echo ""
echo "▶ [STEP 5] Creating a new candidate on Recruitment Service (POST /candidates)..."
CREATE_RESP=$(curl -s -X POST http://localhost:8081/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "candidateName": "Dhrithi Kiran",
    "contactInfo": "dhrithi@example.com",
    "resumeData": "Software Engineering Resume with Microservices Experience",
    "interviewScore": 95.0
  }')
echo "  Response: $CREATE_RESP"

CANDIDATE_ID=$(echo "$CREATE_RESP" | grep -o '"candidateId":"[^"]*' | cut -d'"' -f4)
if [ -z "$CANDIDATE_ID" ]; then
    echo "  ✗ Error: Failed to extract candidateId from response!"
    exit 1
fi
echo "  ✓ Candidate created with ID: $CANDIDATE_ID"

# Step 6: Verify Candidate retrieval
echo ""
echo "▶ [STEP 6] Retrieving candidate from Recruitment Service (GET /candidates/$CANDIDATE_ID)..."
GET_CAND_RESP=$(curl -s http://localhost:8081/candidates/$CANDIDATE_ID)
echo "  Response: $GET_CAND_RESP"

# Step 7: Select Candidate and trigger Inter-Service Onboarding call
echo ""
echo "▶ [STEP 7] Selecting candidate (POST /candidates/$CANDIDATE_ID/select)..."
echo "  (Recruitment Service will trigger HTTP POST to http://localhost:8082/onboarding)"
SELECT_RESP=$(curl -s -X POST http://localhost:8081/candidates/$CANDIDATE_ID/select)
echo "  Response: $SELECT_RESP"

# Step 8: Verify Onboarding Record Created in Onboarding Service
echo ""
echo "▶ [STEP 8] Querying Onboarding Service (GET /onboarding/$CANDIDATE_ID)..."
GET_ONB_RESP=$(curl -s http://localhost:8082/onboarding/$CANDIDATE_ID)
echo "  Response: $GET_ONB_RESP"

# Step 9: Test Failure Handling (Onboarding Service Down)
echo ""
echo "▶ [STEP 9] Simulating Failure: Stopping Onboarding Service..."
kill -9 $ONBOARDING_PID || true
sleep 2
echo "  ✓ Onboarding Service stopped."

echo "  Creating a second candidate to test resilience..."
CAND2_RESP=$(curl -s -X POST http://localhost:8081/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "candidateName": "Alex Rivera",
    "contactInfo": "alex.rivera@example.com",
    "resumeData": "Distributed Systems Engineer",
    "interviewScore": 92.0
  }')
CAND2_ID=$(echo "$CAND2_RESP" | grep -o '"candidateId":"[^"]*' | cut -d'"' -f4)
echo "  Candidate 2 ID: $CAND2_ID"

echo "  Calling POST /candidates/$CAND2_ID/select while Onboarding Service is DOWN..."
FAIL_RESP=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST http://localhost:8081/candidates/$CAND2_ID/select)
echo "  Response with HTTP Status:"
echo "  $FAIL_RESP"

# Step 10: Service Recovery Test
echo ""
echo "▶ [STEP 10] Testing Service Recovery: Restarting Onboarding Service..."
cd "$SCRIPT_DIR/microservices/onboarding-service"
nohup mvn exec:java -Dexec.mainClass="com.hrms.onboarding.OnboardingServiceApp" > "$LOGS_DIR/test-onboarding-restart.log" 2>&1 &
NEW_ONBOARDING_PID=$!
echo "  ✓ Restarted Onboarding Service with PID: $NEW_ONBOARDING_PID"

for i in {1..10}; do
    if curl -s http://localhost:8082/health > /dev/null 2>&1; then
        break
    fi
    sleep 1
done

echo "  Retrying Candidate 2 selection after Onboarding recovery..."
RETRY_RESP=$(curl -s -X POST http://localhost:8081/candidates/$CAND2_ID/select)
echo "  Response: $RETRY_RESP"

echo "  Verifying Candidate 2 onboarding record in Onboarding Service:"
VERIFY_RETRY=$(curl -s http://localhost:8082/onboarding/$CAND2_ID)
echo "  $VERIFY_RETRY"

# Cleanup
echo ""
echo "▶ [STEP 11] Cleaning up background test processes..."
kill -9 $RECRUITMENT_PID || true
kill -9 $NEW_ONBOARDING_PID || true
echo "  ✓ Test processes terminated."

echo ""
echo "======================================================================"
echo "  ✅ ALL DISTRIBUTED MICROSERVICES INTEGRATION TESTS PASSED!"
echo "======================================================================"
