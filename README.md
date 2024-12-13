# sp-exp

# Prerequisites

1. Install java 21 (Temurin was used locally, OpenJdk - on CircleCI)
2. Maven + wrapper was used, so `./mvnw  ...` or maven from IDEA can be used.

# To run
## Locally
1. To run tests - `mvn clean verify` (or `./mvnw clean verify`)
2. To generate and see the test report `mvn allure:serve`

## On CircleCI
1. Project https://app.circleci.com/pipelines/github/voidgit/sp-exp
2. Latest report <to update>
