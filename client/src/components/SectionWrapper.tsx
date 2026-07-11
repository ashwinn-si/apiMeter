import type { ReactNode } from 'react';
import '../index.css';


interface SectionWrapperProps {
  title: string;
  children: ReactNode;
  response?: string | null;
  loading?: boolean;
}

export default function SectionWrapper({
  title,
  children,
  response,
  loading = false,
}: SectionWrapperProps) {
  return (
    <div className="section-wrapper">
      <h3>{title}</h3>
      <div className="relative space-y-4">
        {loading && (
          <div className="loading-overlay">
            <div className="loading-content">
              <svg
                className="h-5 w-5 animate-spin"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                />
              </svg>
              <span className="font-medium">Loading...</span>
            </div>
          </div>
        )}

        <div className={loading ? 'section-body disabled' : 'section-body'}>{children}</div>
      </div>

      {response && (
        <div className="section-response">
          <p className="response-title">Response:</p>
          <pre className="break-words whitespace-pre-wrap">
            {response}
          </pre>
        </div>
      )}
    </div>
  );
}
