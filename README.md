# Violation Comments Lib

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-lib)

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

The templating language is [Mustache](https://github.com/spullara/mustache.java) and may look like:

```
**Reporter**: {{violation.reporter}}{{#violation.rule}}

**Rule**: {{violation.rule}}{{/violation.rule}}
**Severity**: {{violation.severity}}
**File**: {{changedFile.filename}} L{{violation.startLine}}{{#violation.source}}

**Source**: {{violation.source}}{{/violation.source}}

{{violation.message}}
```

You can avoid escaping (replaces new lines with `&#10;`) by using triple `{` like this:

```
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
