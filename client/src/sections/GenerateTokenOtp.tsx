import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

export default function GenerateTokenOtp() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleGenerateOtp = async () => {
    if (!email.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.post(API_ENDPOINTS.GENERATE_TOKEN_OTP, {
        email,
      });

      setResponse(JSON.stringify(res, null, 2));
      setEmail('');
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SectionWrapper title="Generate OTP For Token" response={response} loading={loading}>
      <div>
        <label htmlFor="gen-token-email" className="block text-sm font-medium text-gray-700 mb-1">
          Email Address
        </label>
        <input
          id="gen-token-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
          className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
        />
      </div>

      <button
        onClick={handleGenerateOtp}
        disabled={loading || !email.trim()}
        className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {loading ? 'Generating...' : 'Generate OTP For Token'}
      </button>
    </SectionWrapper>
  );
}
