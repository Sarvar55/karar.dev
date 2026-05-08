#!/bin/bash

# Check if there are any changes to commit
if [ -z "$(git status --porcelain)" ]; then
  echo "No changes to commit."
  exit 0
fi

# Ask the committer agent to do the job
pi subagent committer "Please stage all changes, analyze the diff, generate a concise conventional commit message, and execute the commit."
