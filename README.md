# payment-processing-api
This is a backend REST API for a simplified payment processing system.
```
# register user
curl -X POST localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d '{"username": "Thawan", "password": "12345678"}'

# login user
curl -X POST localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d '{"username": "Thawan", "password": "12345678"}'

# refresh token
curl -X POST localhost:8080/api/v1/auth/refresh -H "Content-Type: application/json" -d '{"refreshToken": "aaaa-bbbb-cccc-dddd"}'

# logout
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <access-token>"

# create transaction
  curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" -H "Authorization: Bearer <access-token>" \
  -H "Idempotency-Key: "1111-2222-3333-4444" \
  -d '{"amount": <amount>, "currency": <currency>}'

# get transaction
  curl http://localhost:8080/api/v1/transactions/<ID> \
  -H "Authorization: Bearer <access-token>" \

# get all transaction
  curl http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer <access-token>" \

```