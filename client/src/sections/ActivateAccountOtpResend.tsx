import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

export default function ActivateAccountOtpResend() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleResendOtp = async () => {
    if (!email.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.post(API_ENDPOINTS.RESEND_ACTIVATION_OTP, {
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
    <SectionWrapper title="Resend Activation OTP" response={response} loading={loading}>
      <div>
        <label htmlFor="resend-email">Email Address</label>
        <input
          id="resend-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>

      <button
        onClick={handleResendOtp}
        disabled={loading || !email.trim()}
      >
        {loading ? 'Sending...' : 'Resend OTP'}
      </button>
    </SectionWrapper>
  );
}
