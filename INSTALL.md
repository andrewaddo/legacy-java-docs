# Installation guide

## Set up base legacy app

```bash
git clone https://github.com/shashirajraja/shopping-cart
rm -rf shopping-cart/.git
rm -rf shopping-cart/.github
rm -rf shopping-cart/.settings
```

## Set up BMAD

```bash
npx bmad-method install
## select "BMad Agile Core System"
## select "Gemini CLI" for the IDE options
```

## Generate documentations

```prompts
@.bmad-core/agents/pm.md *create-brownfield-prd
```

##


## Generate tests - with guided instructions

```prompts
generate all other tests for the existing code
```

1. Run the test `mvn -f shopping-cart/pom.xml test`
2. Insist on the task to generate all tests even when the agent wants to refactor or modernize the code

### Result

1. The agent generated most tests (93) smoothly without assistant. The agent faced few issues but could resolve them itself.
2. There are some untestable logics, which would need code refactoring. We skipped those tests for the current scope of the project.

```logs
* Phase 1 (Services): Complete. All testable methods in all six service classes are now covered by unit tests.
Excellent! All 67 tests passed.
* Phase 2 (Web Layer): Complete. All testable servlets have been identified and are now covered by tests.
Excellent! All 82 tests passed.
* This completes the testing for the IDUtil and MailMessage classes and concludes
  Phase 3 of our testing plan.
Success! All 93 tests passed
```
