# SonarQube Cloud

Static analysis and coverage of every push and pull request, with a quality gate that blocks merging bad code.

## How it is wired
- `services/invoices/pom.xml`: JaCoCo writes `target/site/jacoco/jacoco.xml` during `verify`; the `sonar.*` properties point to the organization and project on sonarcloud.io.
- `.github/workflows/ci.yml`: after `./mvnw verify`, the step *SonarQube Cloud analysis* runs `sonar-maven-plugin:sonar`. It is skipped when the `SONAR_TOKEN` secret does not exist (e.g. pull requests from forks).
- Automatic Analysis is **off** on sonarcloud.io: Java needs the compiled classes and the coverage report, so analysis runs in CI.

## One-time setup
1. Log in to <https://sonarcloud.io> with GitHub.
2. Import the GitHub account as an organization (key `victorbicego`, Free plan); give the SonarQube Cloud GitHub App access to this repository only.
3. Analyze new project → `debt-collection-platform`. New code definition: *Number of days = 30*.
4. Project → Administration → Analysis Method: turn **Automatic Analysis off**.
5. My Account → Security → generate a token; add it to GitHub as repository secret `SONAR_TOKEN`.
6. Check that Project Information shows the same organization and project key as `pom.xml`.
7. GitHub → Settings → Rules → Rulesets: protect `main`, require a pull request and the status checks `invoices` and `SonarCloud Code Analysis`.

## Day to day
- Work on a branch, open a PR: SonarQube Cloud comments the result on the PR.
- Quality gate red → fix the new issues or the missing coverage on new code, push again.
- IntelliJ: plugin *SonarQube for IDE* in connected mode with the same organization shows the issues before committing.

## When it breaks
| Symptom | Cause / fix |
|---------|-------------|
| `Project not found` | Organization or project key in `pom.xml` differs from sonarcloud.io. |
| `You're not authorized to run analysis` | `SONAR_TOKEN` missing, expired or from another account. |
| `Automatic Analysis is enabled` error | Turn it off (step 4). |
| Coverage 0% | `jacoco.xml` missing: the analysis step must run after `verify`. |
