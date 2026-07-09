import type { ReactNode } from 'react';

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
    <div className="bg-white p-6 border rounded-md shadow-sm">
      <h3>{title}</h3>
      <div className="relative space-y-4">
        {loading && (
          <div className="absolute inset-0 z-10 flex items-center justify-center rounded-md bg-white/70 backdrop-blur-sm">
            <div className="flex items-center gap-2 text-blue-600">
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

        <div className={loading ? 'pointer-events-none opacity-60' : ''}>{children}</div>
      </div>

      {response && (
        <div className="mt-6 p-4 bg-gray-50 border border-gray-200 rounded-md">
          <p className="text-sm font-semibold text-gray-600 mb-2">Response:</p>
          <pre className="text-sm text-gray-800 break-words whitespace-pre-wrap font-mono">
            {response}
          </pre>
        </div>
      )}
    </div>
  );
}
