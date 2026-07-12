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
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <polyline points="3 6 5 6 21 6" />
            <path d="M19 6l-1 14H6L5 6" />
            <path d="M10 11v6M14 11v6" />
            <path d="M9 6V4h6v2" />
          </svg>
          Clear DB
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

      <div className="tab-content" key={activeTab}>
        {activeTab === 'account' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <CreateAccount />
            <ActivateAccount />
            <ActivateAccountOtpResend />
          </div>
        )}

        {activeTab === 'token' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <GenerateTokenOtp />
            <GenerateTokenOtpVerification />
          </div>
        )}

        {activeTab === 'rate-limits' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <UsageInfo />
            <GetResponseContentTokenBucket />
            <GetResponseContentSlidingWindow />
          </div>
        )}
      </div>
    </div>
  );
}
