// /postcss.config.cjs
// why: Tailwind runs as a PostCSS plugin; Vite picks this up automatically
module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {}
  }
};
