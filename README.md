# payment-processing-api
This is a backend REST API for a simplified payment processing system.
```
# register user
curl -X POST localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d '{"username": "Thawan", "password": "12345678"}'

# login user
curl -X POST localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d '{"username": "Thawan", "password": "12345678"}'
```