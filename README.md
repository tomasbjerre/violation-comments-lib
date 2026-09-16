# Violation Comments Lib

[![Maven Central](https://img.shields.io/maven-central/v/se.bjurr.violations/violation-comments-lib.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/se.bjurr.violations/violation-comments-lib)

This is a library that helps working with comments from static code analysis.

It supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It is used by these libraries:

* [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib).
* [Violation Comments to GitLab Lib](https://github.com/tomasbjerre/violation-comments-to-gitlab-lib).
* [Violation Comments to Bitbucket Server Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-server-lib).
* [Violation Comments to Bitbucket Cloud Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-cloud-lib)

## Template

It uses a template to render each violation comment. There is a default that can be replaced with a custom template.

The context available when the template is rendered is:

* `violation` that is an instance of [Violation](src/main/java/se/bjurr/violations/comments/lib/model/ViolationData.java).
* `changedFile` that is an instance of [ChangedFile](src/main/java/se/bjurr/violations/comments/lib/model/ChangedFile.java).

The templating language is [Handlebars](https://github.com/jknack/handlebars.java) and may look like:

```hbs
**Reporter**: {{violation.reporter}}{{#violation.rule}}

**Rule**: {{violation.rule}}{{/violation.rule}}
**Severity**: {{violation.severity}}
**File**: {{changedFile.filename}} L{{violation.startLine}}{{#violation.source}}

**Source**: {{violation.source}}{{/violation.source}}

{{violation.message}}
```

You can avoid escaping (replaces new lines with `&#10;`) by using triple `{` like this:

```hbs
{{{violation.message}}}
```

When using command line tools you may have problems with the ` (accent) character. You can do:

```bash
...
-comment-template "
message: 
\\\`\\\`\\\`
{{{violation.message}}}
\\\`\\\`\\\`
"
```

And it will surround the `message` with triple ```.

### Helpers

The Handlebars library comes with [helper methods](https://github.com/jknack/handlebars.java/tree/master/handlebars/src/main/java/com/github/jknack/handlebars/helper), these are registered:

* `ConditionalHelpers`
* `IfHelper`
* `StringHelpers`
* `UnlessHelper`

## Summary comment

A `CommentsProvider` can opt in to a separate summary comment, distinct from the per-violation comments and from the single accumulated comment made by `shouldCreateCommentWithAllSingleFileComments`. It is off by default, so existing implementations are unaffected; enable it by overriding `shouldCreateSummaryComment()` to return `true`.

The context available when the summary template is rendered is:

* `summary` that is an instance of [SummaryData](src/main/java/se/bjurr/violations/comments/lib/model/SummaryData.java), exposing `violationsCount`, `filesCount`, `violations`, `perSeverity` (list of `severity`/`count`) and `perReporter` (list of `reporter`/`count`).

The default template looks like:

```hbs
**Summary**: {{summary.violationsCount}} violation(s) found in {{summary.filesCount}} file(s).
{{#summary.perSeverity}}
- **{{severity}}**: {{count}}
{{/summary.perSeverity}}
```

It can be replaced by overriding `findSummaryCommentTemplate()`, the same way the per-violation template is replaced with `findCommentTemplate()`.
