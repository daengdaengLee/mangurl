FROM golang:1.21.6-alpine3.19 as serverless-build
WORKDIR /app/mangurl/serverless
# Copy dependencies list
COPY serverless/go.mod serverless/go.sum ./
# Build
COPY serverless/main.go .
RUN go build -o main main.go

# Copy artifacts to a clean image
FROM alpine:3.19

WORKDIR /app/mangurl/serverless
COPY --from=serverless-build /app/mangurl/serverless/main /app/mangurl/serverless/main

ENTRYPOINT [ "/app/mangurl/serverless/main" ]
