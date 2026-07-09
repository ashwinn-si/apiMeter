# API Meter Frontend Instructions and Rules

This document outlines the rules and design patterns to be followed when contributing to the API Meter frontend project.

## UI Design Rules
1. **Minimalistic Approach**: The UI should remain simple, clean, and minimalistic. Do NOT use fancy design patterns, glassmorphism, heavy gradients, or excessive animations.
2. **Top-to-Bottom Flow**: Use standard flex-col or stack layouts to naturally flow content from top to bottom.
3. **Tailwind CSS**: Use Tailwind CSS for utility classes. Keep styles standardized using standard spacing, plain white backgrounds for containers, and minimal borders.

## Component Architecture
1. **Section Wrapper**: Always use the `SectionWrapper` component located at `src/components/SectionWrapper.tsx` for form sections.
    - It enforces the minimalistic border/shadow styling.
    - It standardizes the title rendering.
    - It natively handles rendering API responses using the `response` prop.
    - It handles the generic loading overlay state via the `loading` prop.
    
2. **API Integrations**: 
    - Use the configured `apiHelper` instance from `src/utils/apiHelper.ts` for making HTTP requests. It handles custom response structures and toast notifications seamlessly.
    - Always reference API URLs from `src/utils/constants.ts` (`API_ENDPOINTS`).

## State Management
- For form-based sections, keep state local (`useState`).
- When a form is submitted, always capture the response and pass it to `SectionWrapper` as a string (typically using `JSON.stringify()`) so it renders properly at the bottom.
- Also capture loading states and pass the `loading` boolean to the `SectionWrapper`.
