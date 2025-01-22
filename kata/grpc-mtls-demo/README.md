# gRPC mTLS demo


## build

```bash
mvn clean install
```

## Generate Certificates
```bash
mkdir -p /tmp/certs && cd /tmp/certs
# Generate CA Key and Cert
openssl genrsa -out ca.key 2048
openssl req -new -x509 -key ca.key -out ca.crt -subj "/CN=CA"

# Generate Server Key and CSR
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -subj "/CN=localhost"

# Sign Server Cert with CA
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -set_serial 1 -out server.crt

# Generate Client Key and CSR
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr -subj "/CN=client"

# Sign Client Cert with CA
openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -set_serial 2 -out client.crt

# Convert Keys to PEM
openssl pkcs8 -topk8 -nocrypt -in server.key -out server.pem
openssl pkcs8 -topk8 -nocrypt -in client.key -out client.pem
```

## run

```
cd server
mvn exec:java -Dexec.mainClass="com.fanyamin.grpc_demo.GrpcServer"

cd client
mvn exec:java -Dexec.mainClass="com.fanyamin.grpc_demo.GrpcClient"

```