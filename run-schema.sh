#!/bin/bash
# Execute schema.sql on Railway database

echo "Connecting to Railway PostgreSQL database..."
echo ""

PGPASSWORD=mGnCZTdilSEekfHOmavezeECINsUloMq \
  "/c/Program Files/PostgreSQL/18/bin/psql.exe" \
  -h interchange.proxy.rlwy.net \
  -p 57338 \
  -U postgres \
  -d railway \
  -f src/main/resources/schema.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Schema executed successfully!"
else
    echo ""
    echo "✗ Failed to execute schema."
fi
