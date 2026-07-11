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
        <label htmlFor="activate-email" className="block text-sm font-medium text-gray-700 mb-1">
          Email Address
        </label>
        <input
          id="activate-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
          className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
        />
      </div>

      <div>
        <label htmlFor="activate-otp" className="block text-sm font-medium text-gray-700 mb-1">
          OTP
        </label>
        <input
          id="activate-otp"
          type="number"
          placeholder="123456"
          value={otp}
          onChange={e => setOtp(e.target.value)}
          disabled={loading}
          className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
        />
      </div>

      <button
        onClick={handleActivateAccount}
        disabled={loading || !email.trim() || !otp.trim()}
        className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {loading ? 'Activating...' : 'Activate'}
      </button>
    </SectionWrapper>
  );
}
