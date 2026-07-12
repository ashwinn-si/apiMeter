import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

interface TokenResponse {
  token: string;
}

export default function GetResponseContentTokenBucket() {
  const [token, setToken] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleGetData = async () => {
    if (!token.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.get<TokenResponse>(
        API_ENDPOINTS.GENERATE_RESPONSE_DATA_TOKEN_BUCKET + `/${token}`,
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
    <SectionWrapper
      title="Token Bucket — Generate Response"
      response={response}
      loading={loading}
    >
      <p className="section-hint">Paste the Bearer token obtained from the Verify OTP step.</p>
      <div>
        <label htmlFor="token-bucket-token">Bearer Token</label>
        <input
          id="token-bucket-token"
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
        {loading ? 'Fetching...' : 'Send Request'}
      </button>
    </SectionWrapper>
  );
}
