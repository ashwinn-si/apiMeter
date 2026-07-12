import { useEffect, useState } from 'react';
import apiHelper, { type ApiResponse } from '../utils/apiHelper';
import { API_ENDPOINTS } from '../utils/constants';
import SectionWrapper from '../components/SectionWrapper';

interface CreateAccountResponse {
  id: number;
  email: string;
}

interface SubscriptionResponse {
  id: number;
  value: string;
}

export default function CreateAccount() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<string | null>(null);

  const [subscriptions, setSubscriptions] = useState<SubscriptionResponse[]>([]);
  const [selectedSubscriptionId, setSelectedSubscriptionId] = useState<number | null>(null);

  const handleCreateAccount = async () => {
    if (!email.trim() || selectedSubscriptionId === null) return;

    try {
      setLoading(true);
      setResponse(null);

      const user = await apiHelper.post<CreateAccountResponse>(API_ENDPOINTS.CREATE_ACCOUNT, {
        email,
        subscriptionId: selectedSubscriptionId,
      });

      console.log(user);
      setResponse(JSON.stringify(user, null, 2));
    } catch (error: any) {
      setResponse(error?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const getAllSubscription = async () => {
    try {
      const subscriptionDetails = await apiHelper.get<SubscriptionResponse[]>(
        API_ENDPOINTS.GET_ALL_SUBSCRIPTION
      );

      if (!subscriptionDetails || !('data' in subscriptionDetails)) {
        throw new Error(subscriptionDetails?.message || 'Failed to load subscriptions');
      }

      const { data } = subscriptionDetails as ApiResponse<SubscriptionResponse[]>;
      setSubscriptions(data);

      if (data.length > 0) {
        setSelectedSubscriptionId(data[0].id);
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    getAllSubscription();
  }, []);

  return (
    <SectionWrapper title="Create Account" response={response} loading={loading}>
      <div>
        <label
          htmlFor="create-email"
          style={{
            display: 'block',
            fontSize: '14px',
            fontWeight: 600,
            marginBottom: '6px',
          }}
        >
          Email Address
        </label>

        <input
          id="create-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
          style={{
            width: '100%',
            padding: '10px',
            border: '1px solid #ccc',
            borderRadius: '6px',
            marginBottom: '20px',
            boxSizing: 'border-box',
          }}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <div
          style={{
            fontSize: '14px',
            fontWeight: 600,
            marginBottom: '10px',
          }}
        >
          Select Subscription
        </div>

        <div
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            gap: '12px',
          }}
        >
          {subscriptions.map(subscription => {
            const selected = selectedSubscriptionId === subscription.id;

            return (
              <div
                key={subscription.id}
                onClick={() => setSelectedSubscriptionId(subscription.id)}
                style={{
                  cursor: 'pointer',
                  border: selected ? '2px solid #2563eb' : '1px solid #d1d5db',
                  backgroundColor: selected ? '#dbeafe' : '#fff',
                  borderRadius: '8px',
                  padding: '16px',
                  minWidth: '150px',
                  transition: 'all 0.2s ease',
                  userSelect: 'none',
                }}
              >
                <div
                  style={{
                    fontSize: '13px',
                    color: '#666',
                  }}
                >
                  {subscription.value}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <button
        onClick={handleCreateAccount}
        disabled={loading || !email.trim() || selectedSubscriptionId === null}
        style={{
          width: '100%',
          padding: '12px',
          backgroundColor:
            loading || !email.trim() || selectedSubscriptionId === null ? '#9ca3af' : '#2563eb',
          color: '#fff',
          border: 'none',
          borderRadius: '6px',
          cursor:
            loading || !email.trim() || selectedSubscriptionId === null ? 'not-allowed' : 'pointer',
          fontSize: '15px',
          fontWeight: 600,
        }}
      >
        {loading ? 'Creating...' : 'Create Account'}
      </button>
    </SectionWrapper>
  );
}
