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
              <div className="loading-spinner" />
              <div className="loading-label">Loading…</div>
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
