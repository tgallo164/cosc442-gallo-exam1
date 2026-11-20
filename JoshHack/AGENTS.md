# JoshHack project guidance

- Use JDK 17 and the Maven wrapper (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS).
- Before considering work complete, run JUnit (`test`), PIT (`pitest:mutationCoverage`), and SpotBugs (`compile spotbugs:spotbugs`) with the Maven wrapper.
- Preserve existing behavior unless a change is explicitly requested.
- Do not weaken, remove, or disable tests just to make the build pass.
- Clearly identify production-code changes and their impact for human review.
- Do not push or merge changes automatically.
