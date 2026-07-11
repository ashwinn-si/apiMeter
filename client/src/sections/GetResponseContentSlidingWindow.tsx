import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

interface TokenResponse {
  token: string;
}

export default function GetResponseContentSlidingWindow() {
  const [token, setToken] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleGetData = async () => {
    if (!token.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.get<TokenResponse>(
        API_ENDPOINTS.GENERATE_RESPONSE_DATA_SLIDING_WINDOW + `/${token}`,
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
      title="Generate Response Data [Sliding Window]"
      response={response}
      loading={loading}
    >
      <p>Get the token from the above request</p>
      <div>
        <label htmlFor="verify-email" className="block text-sm font-medium text-gray-700 mb-1">
          Token
        </label>
        <input
          id="verify-token"
          type="text"
          value={token}
          onChange={e => setToken(e.target.value)}
          disabled={loading}
          className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
        />
      </div>

      <button
        onClick={handleGetData}
        disabled={loading || !token.trim()}
        className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {loading ? 'Generating...' : 'Generate Data'}
      </button>
    </SectionWrapper>
  );
}
