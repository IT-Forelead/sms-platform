#!/bin/bash

export POSTGRES_HOST="localhost"
export POSTGRES_PORT=5430
export POSTGRES_USER="sms_platform"
export POSTGRES_PASSWORD="123"
export POSTGRES_DATABASE="sms_platform"
export POSTGRES_POOL_SIZE=1024
export HTTP_HEADER_LOG=false
export HTTP_BODY_LOG=false
export HTTP_HOST="localhost"
export HTTP_PORT=9000
export REDIS_SERVER_URI="redis://localhost"
export ACCESS_TOKEN_SECRET_KEY=dah3EeJ8xohtaeJ5ahyah-
export JWT_SECRET_KEY=dah3EeJ8xohtaeJ5ahyah-
export JWT_TOKEN_EXPIRATION=30.minutes
export ADMIN_USER_TOKEN=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjogImI4ZTgxYjZlLWFhOTYtMTFlYy1iOTA5LTAyNDJhYzEyMDAwMiJ9._VRMCSQRGWDqbRBjRmy-62nyG3IuInHw0Cs2h08VaRI
export PASSWORD_SALT=06!grsnxXG0d*Pj496p6fuA*o
export MESSAGE_BROKER_API="https://api.playmobile.uz"
export MESSAGE_BROKER_USERNAME="test"
export MESSAGE_BROKER_PASSWORD="test_secret"
export MESSAGE_BROKER_ENABLED=false
export SCHEDULER_START_TIME="9:00 AM"
export SCHEDULER_PERIOD=1.day
export APP_ENV=test