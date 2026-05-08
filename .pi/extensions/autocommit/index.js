module.exports = (context) => {
  return {
    commands: {
      commit: {
        description: 'Stage all changes and generate a commit message via AI subagent',
        run: async (args) => {
          const { execSync } = require('child_process');
          
          try {
            // 1. Check for changes
            const status = execSync('git status --porcelain').toString().trim();
            if (!status) {
              console.log('ℹ️ Nothing to commit.');
              return;
            }

            console.log('🚀 Analyzing changes and preparing commit...');

            // 2. Delegate to the 'committer' subagent
            await context.subagent({
              agent: 'committer',
              task: 'Stage all changes (including untracked), generate a high-quality conventional commit message from the diff, and execute the commit.'
            });

            console.log('✅ Git commit process completed.');
          } catch (error) {
            console.error('❌ Error during commit process:', error.message);
          }
        }
      }
    }
  };
};
