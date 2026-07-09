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
      setEmail('');
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SectionWrapper title="Resend OTP" response={response} loading={loading}>
      <div>
        <label htmlFor="resend-email" className="block text-sm font-medium text-gray-700 mb-1">
          Email Address
        </label>
        <input
          id="resend-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
          className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
        />
      </div>

      <button
        onClick={handleResendOtp}
        disabled={loading || !email.trim()}
        className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {loading ? 'Sending...' : 'Resend OTP'}
      </button>
    </SectionWrapper>
  );
}