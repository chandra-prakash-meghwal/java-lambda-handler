# java-lambda-handler
lambda handler java 11

# how to deploy
Build the project
install maven if not already installed with command `sudo apt install maven`
run the below command to create jar file
```
mvn clean package
```
it will create target directory which will have the jar file with name like my-lambda-function-1.0-SNAPSHOT.jar

Open the AWS Management Console:
Go to the AWS Management Console.

Navigate to AWS Lambda:
Search for "Lambda" in the services menu and select it.

Create a New Function:
Click on the "Create function" button.

Configure the Function:

Function name: Enter a unique name for your function.
Runtime: Select "Java 11" (or the appropriate version you're using).
Role: Choose "Create a new role with basic Lambda permissions" or "Use an existing role" if you have a predefined role.
Click "Create function".

Upload the .jar File:
In the function's configuration page, scroll down to the "Function code" section.

Code entry type: Choose "Upload a .zip or .jar file".
Runtime: Ensure it's set to "Java 11" (or your version).
Handler: Set the handler to the appropriate method in your code. For example:

    com.example.LambdaFunctionHandler::handleRequest


Upload: Click "Upload" and select your .jar file.

Add Environment Variables:
Scroll down to the "Environment variables" section and add the following variables like database credentials as used in the code
MYSQL_URL
MYSQL_USER
MYSQL_PASSWORD
REST_API_URL

IAM Role Permissions:
Ensure your Lambda function's IAM role has the necessary permissions to interact with Redis, MySQL, and make HTTP requests. Attach the following managed policies:
    AmazonRDSFullAccess
    AWSLambdaBasicExecutionRole

Modify Permissions:
If necessary, customize the IAM policy attached to your role to include any specific permissions required by your function.

Configure SnapStart

Enable SnapStart:
    In the Lambda function's configuration page, go to the "SnapStart" section.
    Enable SnapStart for your function. This will allow AWS to take a snapshot of your function's state after initialization, leading to faster cold starts.

Test the Function

Create a Test Event:
    Go to the "Test" tab.
    Click "Configure test event".
    Choose "Create new test event".
    Enter a name for the test event and provide a sample JSON payload if needed.
    Click "Create".

Run the Test:
    Click "Test" to invoke your Lambda function.
    Review the results in the console to ensure the function behaves as expected.
