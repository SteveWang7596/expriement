rmdir /S client-server
java -jar openapi-generator-cli-5.0.1.jar generate -i ..\src\main\resources\cardPaymentApi.yaml -g java-undertow-server -o client-server --package-name za.co.transactionjunction.cardpaymentapi --api-package za.co.transactionjunction.cardpaymentapi.gen.api --model-package za.co.transactionjunction.cardpaymentapi.gen.model --invoker-package za.co.transactionjunction.cardpaymentapi.gen.invoke --artifact-id qr-client-junction --group-id za.co.transactionjunction.cardpaymentapi  --additional-properties=dateLibrary=java8