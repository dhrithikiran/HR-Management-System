#!/bin/bash
# ==============================================================================
# Run Recruitment Microservice (Port 8081)
# ==============================================================================
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SERVICE_DIR="$SCRIPT_DIR/microservices/recruitment-service"

export PATH=$PATH:/opt/homebrew/bin:/usr/local/bin

if [ -z "$DB_PASSWORD" ]; then
    echo "ERROR: DB_PASSWORD environment variable is not set."
    echo "Please set DB_PASSWORD before starting the service, e.g.:"
    echo "  export DB_PASSWORD=your_mysql_password"
    exit 1
fi

echo "========================================================"
echo "Starting Recruitment Microservice on http://localhost:8081..."
echo "========================================================"

cd "$SERVICE_DIR"
mvn compile exec:java -Dexec.mainClass="com.hrms.recruitment.RecruitmentServiceApp"
