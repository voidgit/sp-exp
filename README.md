# sp-exp


# To run
## Locally

### Prerequisites
1. Install java 21 (Temurin was used locally, OpenJdk - on CircleCI)
2. Maven + wrapper was used, so `./mvnw  ...` or maven from IDEA can be used.

### Running
1. To run tests - `mvn clean verify` (or `./mvnw clean verify`)
2. To generate and see the test report `mvn allure:serve`

## On CircleCI
1. Project https://app.circleci.com/pipelines/github/voidgit/sp-exp
2. Latest report https://output.circle-artifacts.com/output/job/274c270f-232d-4fd1-9964-91f5f0efcb4f/artifacts/0/Report/Allure/index.html 
(this is a link to Artifacts in CircleCI)

# Test results
There are 7 tests in total, 3 tests are failing due to actual bugs in the service under test.

Tests that are failing: `shouldRetrieveCreatedComment`, `shouldRejectIncorrectCommentCreation`, `shouldRejectIncorrectPostCreation`

Please find example of bug report for 1 test failure below.


# Test plan (short version)
1. Functional aspects:
   1. Authentication to be tested
   2. Authorization to be tested 
   3. All main business flows should be covered
   4. All endpoints should be covered
   5. Input data validation should be covered on proper agreed upon test pyramid level
      1. Especially data integrity like creating entity without/with incorrect parent (like creating Post with no/incorrect/different user)
      2. Data limits are also very important (e.g. attacker can push hundreds of megabytes of data, and for instance Postgres has tech limit ~1 GiB for `text` (https://www.postgresql.org/docs/current/datatype-character.html) - DB will not reject few first post and will go down)
2. Main non-functional aspects:
   1. Performance capacity, resources utilization and response times should be tested
   2. Reliability should be tested (e.g. few days of constant high load)
   3. Security needs to be tested (static - like libraries vulnerabilities, dynamic - like encryption at rest (in DB), encryption in transit - like no HTTP for end user etc.)
   4. Privacy, compliance, GDPR etc.

# Test coverage
1. Only 1 business flow is covered partially - `getting comments for user post` (1.iv from plan)
2. Only 5 endpoints are covered from approximately 30.
3. Other main functional aspects are not covered.
4. Non-functional aspects are not covered at all.

Test report can be found here with tests mapped to stories - https://output.circle-artifacts.com/output/job/274c270f-232d-4fd1-9964-91f5f0efcb4f/artifacts/0/Report/Allure/index.html
(jiras are pseudorandom ones taken from Atlassian Jira Data Center)

# Bug report example for test `shouldRetrieveCreatedComment()`
### Title: Jira-111: When attempting to create comment, 201 is returned but comment is not actually created

**Summary:**

When attempting to create comment with valid data, 201 is returned along with valid data used in the comment creation - 
but comment is not actually created and cannot be found when requesting list of comments.

**Steps to reproduce:**

1. Run test `shouldRetrieveCreatedComment()`

or

1. Make POST request to `https://jsonplaceholder.typicode.com/comments`, with body with valid data like:
```
{
    "postId": 1,
    "id": null,
    "name": "valid name 508b1f9e-a8a1-4fe5-8cc1-197a334e4da5",
    "email": "valid_test_email@restmail.net",
    "body": "valid body"
}
```
2. Get list of comments via GET `https://jsonplaceholder.typicode.com/comments`
3. Find the comment that was just created in the list say via unique name

**Actual result:**

POST `https://jsonplaceholder.typicode.com/comments` results with 201 + valid response exactly the same as data that was used in comment creation + valid ID (that's part is ok)
When trying to find the comment in the list returned via GET `https://jsonplaceholder.typicode.com/comments` - _it is not there_.

**Expected result:**

Comment is created and present when requesting the list of comments.
