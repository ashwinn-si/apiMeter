import { useState } from 'react';
import apiHelper from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

export default function ActivateAccount() {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const handleActivateAccount = async () => {
    if (!email.trim() || !otp.trim()) return;

    try {
      setLoading(true);
      setResponse(null);

      const res = await apiHelper.post(API_ENDPOINTS.ACTIVATE_ACCOUNT, {
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
    <SectionWrapper title="Activate Account" response={response} loading={loading}>
      <div>
        <label htmlFor="activate-email">Email Address</label>
        <input
          id="activate-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>

      <div>
        <label htmlFor="activate-otp">OTP</label>
        <input
          id="activate-otp"
          type="number"
          placeholder="123456"
          value={otp}
          onChange={e => setOtp(e.target.value)}
          disabled={loading}
        />
      </div>

      <button
        onClick={handleActivateAccount}
        disabled={loading || !email.trim() || !otp.trim()}
      >
        {loading ? 'Activating...' : 'Activate Account'}
      </button>
    </SectionWrapper>
  );
}
