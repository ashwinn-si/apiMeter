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
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SectionWrapper title="Generate OTP for Token" response={response} loading={loading}>
      <div>
        <label htmlFor="gen-token-email">Email Address</label>
        <input
          id="gen-token-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>

      <button
        onClick={handleGenerateOtp}
        disabled={loading || !email.trim()}
      >
        {loading ? 'Generating...' : 'Generate OTP'}
      </button>
    </SectionWrapper>
  );
}
