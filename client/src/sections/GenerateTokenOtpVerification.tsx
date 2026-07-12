import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

interface TokenResponse {
  token: string;
}

export default function GenerateTokenOtpVerification() {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleVerifyOtp = async () => {
    if (!email.trim() || !otp.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.post<TokenResponse>(API_ENDPOINTS.VERIFY_TOKEN_OTP, {
        email,
        otp: Number(otp),
      });

      setResponse(JSON.stringify(res, null, 2));
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SectionWrapper title="Verify OTP for Token" response={response} loading={loading}>
      <div>
        <label htmlFor="verify-email">Email Address</label>
        <input
          id="verify-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>

      <div>
        <label htmlFor="verify-otp">OTP</label>
        <input
          id="verify-otp"
          type="number"
          placeholder="123456"
          value={otp}
          onChange={e => setOtp(e.target.value)}
          disabled={loading}
        />
      </div>

      <button
        onClick={handleVerifyOtp}
        disabled={loading || !email.trim() || !otp.trim()}
      >
        {loading ? 'Verifying...' : 'Verify OTP & Get Token'}
      </button>
    </SectionWrapper>
  );
}
