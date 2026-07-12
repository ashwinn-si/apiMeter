import { useState } from 'react';
import CreateAccount from './sections/CreateAccount';
import './index.css';
import ActivateAccount from './sections/ActivateAccount';
import ActivateAccountOtpResend from './sections/ActivateAccountOtpResend';
import GenerateTokenOtp from './sections/GenerateTokenOtp';
import GenerateTokenOtpVerification from './sections/GenerateTokenOtpVerification';
import GetResponseContentTokenBucket from './sections/GetResponseContentTokenBucket';
import GetResponseContentSlidingWindow from './sections/GetResponseContentSlidingWindow';
import apiHelper from './utils/apiHelper';
import { API_ENDPOINTS } from './utils/constants';
import UsageInfo from './sections/UsageInfo';

export default function App() {
  const [activeTab, setActiveTab] = useState<'account' | 'token' | 'rate-limits'>('account');
  function clearDatabase() {
    apiHelper.delete(API_ENDPOINTS.CLEAR_DATABASE);
  }
  return (
    <div className="app-container">
      <div className="app-header">
        <div>
          <h1 className="app-title">API Meter</h1>
          <p className="app-subtitle">Rate-Limiter Testing Dashboard</p>
        </div>
        <button className="clear-button" onClick={() => clearDatabase()}>
          clear database
        </button>
      </div>

      <div className="tab-nav">
        <button
          className={`tab-button ${activeTab === 'account' ? 'active' : ''}`}
          onClick={() => setActiveTab('account')}
        >
          Account Setup
        </button>
        <button
          className={`tab-button ${activeTab === 'token' ? 'active' : ''}`}
          onClick={() => setActiveTab('token')}
        >
          Token Auth
        </button>
        <button
          className={`tab-button ${activeTab === 'rate-limits' ? 'active' : ''}`}
          onClick={() => setActiveTab('rate-limits')}
        >
          Rate Limits
        </button>
      </div>

      <div className="tab-content">
        {activeTab === 'account' && (
          <div className="flex flex-col gap-6">
            <CreateAccount />
            <ActivateAccount />
            <ActivateAccountOtpResend />
          </div>
        )}

        {activeTab === 'token' && (
          <div className="flex flex-col gap-6">
            <GenerateTokenOtp />
            <GenerateTokenOtpVerification />
          </div>
        )}

        {activeTab === 'rate-limits' && (
          <div className="flex flex-col gap-6">
            <UsageInfo />
            <GetResponseContentTokenBucket />
            <GetResponseContentSlidingWindow />
          </div>
        )}
      </div>
    </div>
  );
}
