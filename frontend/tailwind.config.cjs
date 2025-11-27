// /tailwind.config.cjs
// why: content globs enable JIT; safelist protects classes created dynamically at runtime
module.exports = {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  safelist: [
    // status/utility states used by JS toggles or third-party components
    'hidden', 'block', 'flex', 'grid', 'inline-flex',
    'opacity-0', 'opacity-100',
    'pointer-events-none', 'pointer-events-auto',
    // brand/hero tokens seen in the design
    'animate-ping',
    'bg-white/5', 'bg-emerald-400',
    'ring-white/10',
    'text-white/90', 'text-white/80',
    // responsive display helpers that might be toggled
    'md:hidden', 'md:flex', 'lg:block', 'lg:hidden'
  ],
  theme: {
    extend: {
      fontFamily: { display: ['Inter', 'ui-sans-serif', 'system-ui'] },
      colors: {
        lupol: {
          950: '#1A145C'
        }
      }
    }
  },
  corePlugins: {
    // keep default set; disable when you know you don't need features
  },
  darkMode: 'media'
};
