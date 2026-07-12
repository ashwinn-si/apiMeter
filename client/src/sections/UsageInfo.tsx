import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

interface UsageInfoResponse {
  percentage: string;
  noTimesUsed: number;
  allowedCount: number;
  remainingTimeToRefresh: number;
}

export default function UsageInfo() {
  const [token, setToken] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleGetData = async () => {
    if (!token.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.get<UsageInfoResponse>(
        API_ENDPOINTS.USAGE_LIMIT_INFO + `/${token}`,
        {
          headers: {
            Authorization: `Bearer ${token.trim()}`,
          },
        }
      );

      setResponse(JSON.stringify(res, null, 2));
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SectionWrapper title="Usage Limit Info" response={response} loading={loading}>
      <p className="section-hint">Enter your token to check current rate-limit usage and refresh time.</p>
      <div>
        <label htmlFor="usage-token">Bearer Token</label>
        <input
          id="usage-token"
          type="text"
          placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          value={token}
          onChange={e => setToken(e.target.value)}
          disabled={loading}
        />
      </div>

      <button
        onClick={handleGetData}
        disabled={loading || !token.trim()}
      >
        {loading ? 'Fetching...' : 'Check Usage'}
      </button>
    </SectionWrapper>
  );
}
