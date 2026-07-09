import CreateAccount from './sections/CreateAccount';
import ActivateAccount from './sections/ActivateAccount';
import ActivateAccountOtpResend from './sections/ActivateAccountOtpResend';
import GenerateTokenOtp from './sections/GenerateTokenOtp';
import GenerateTokenOtpVerification from './sections/GenerateTokenOtpVerification';

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50 text-gray-900 font-sans p-8">
      <div className="max-w-xl mx-auto space-y-8">
        <div className="border-b pb-4 mb-8">
          <h1 className="text-2xl font-bold">API Meter</h1>
          <p className="text-gray-600 text-sm">Rate-Limiter Frontend</p>
        </div>

        <div className="flex flex-col gap-8">
          <CreateAccount />
          <ActivateAccount />
          <ActivateAccountOtpResend />
          <GenerateTokenOtp />
          <GenerateTokenOtpVerification />
        </div>
      </div>
    </div>
  );
}
