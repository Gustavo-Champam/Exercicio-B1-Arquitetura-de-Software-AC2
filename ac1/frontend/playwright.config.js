import { defineConfig } from '@playwright/test';
export default defineConfig({ testDir: './tests', timeout: 60000, fullyParallel: true, use: { baseURL: process.env.APP_URL || 'http://localhost:5173', trace: 'retain-on-failure', screenshot: 'only-on-failure' }, reporter: [['list'], ['html', {open:'never'}]] });
