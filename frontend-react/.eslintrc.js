module.exports = {
  extends: ['ts-react-important-stuff', 'plugin:prettier/recommended'],
  plugins: ['simple-import-sort'],
  rules: {
    'prettier/prettier': ['warn', { singleQuote: true, printWidth: 120, endOfLine: 'auto' }],
  },
  overrides: [
    {
      files: ['*.ts', '*.tsx'],
      parser: '@typescript-eslint/parser',
      plugins: ['simple-import-sort', '@typescript-eslint'],
      extends: ['plugin:react/recommended', 'plugin:@typescript-eslint/recommended', 'plugin:prettier/recommended'],
      parserOptions: {
        ecmaversion: 2018,
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true,
        },
      },
      settings: {
        react: {
          version: 'detect',
        },
      },

      /**
       * Typescript Rules
       */
      rules: {
        'prettier/prettier': ['warn', { singleQuote: true, printWidth: 120, endOfLine: 'auto' }],
        '@typescript-eslint/no-explicit-any': 'off',
        'react/react-in-jsx-scope': 'off',
        '@typescript-eslint/no-non-null-assertion': 'off',
        '@typescript-eslint/ban-ts-comment': 'off',
        '@typescript-eslint/no-unused-vars': [
          'warn',
          {
            ignoreRestSiblings: true,
            argsIgnorePattern: '^_',
            varsIgnorePattern: '^_',
            caughtErrorsIgnorePattern: '^_',
          },
        ],
        'no-console': 'off',
        '@typescript-eslint/no-empty-function': ['error', { allow: ['arrowFunctions'] }],
        'no-unused-vars': 'off',
        'react/prop-types': 'off',
      },
    },
  ],
};
