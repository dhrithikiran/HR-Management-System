#!/bin/bash
# ==============================================================================
# Run All Microservices in Background
# ==============================================================================
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export PATH=$PATH:/opt/homebrew/bin:/usr/local/bin

if [ -z "$DB_PASSWORD" ]; then
    echo "ERROR: DB_PASSWORD environment variable is not set."
    echo "Please set DB_PASSWORD before starting the services, e.g.:"
    echo "  export DB_PASSWORD=your_mysql_password"
    exit 1
fi

LOGS_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOGS_DIR"

echo "========================================================"
echo " Starting HRMS Distributed Microservices"
echo "========================================================"

# 1. Start Onboarding Service on Port 8082
echo "[1/2] Launching Onboarding Service (Port 8082)..."
cd "$SCRIPT_DIR/microservices/onboarding-service"
nohup mvn compile exec:java -Dexec.mainClass="com.hrms.onboarding.OnboardingServiceApp" > "$LOGS_DIR/onboarding-service.log" 2>&1 &
ONBOARDING_PID=$!
echo "      Onboarding Service PID: $ONBOARDING_PID"

# 2. Start Recruitment Service on Port 8081
echo "[2/2] Launching Recruitment Service (Port 8081)..."
cd "$SCRIPT_DIR/microservices/recruitment-service"
nohup mvn compile exec:java -Dexec.mainClass="com.hrms.recruitment.RecruitmentServiceApp" > "$LOGS_DIR/recruitment-service.log" 2>&1 &
RECRUITMENT_PID=$!
echo "      Recruitment Service PID: $RECRUITMENT_PID"

echo ""
echo "Waiting for services to become healthy..."
sleep 4

# Check health
curl -s http://localhost:8082/health || true
echo ""
curl -s http://localhost:8081/health || true
echo ""

echo "========================================================"
echo " Services are running in the background."
echo " Logs:"
echo "   - Onboarding Service:  $LOGS_DIR/onboarding-service.log"
echo "   - Recruitment Service: $LOGS_DIR/recruitment-service.log"
echo " To stop services, run: kill $ONBOARDING_PID $RECRUITMENT_PID"
echo "========================================================"
