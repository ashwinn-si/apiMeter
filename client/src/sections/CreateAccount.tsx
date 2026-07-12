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
        <label htmlFor="create-email">Email Address</label>
        <input
          id="create-email"
          type="email"
          placeholder="name@example.com"
          value={email}
          onChange={e => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>

      <div>
        <label>Select Subscription</label>
        <div className="subscription-grid">
          {subscriptions.map(subscription => {
            const selected = selectedSubscriptionId === subscription.id;
            return (
              <div
                key={subscription.id}
                onClick={() => setSelectedSubscriptionId(subscription.id)}
                className={`subscription-card ${selected ? 'selected' : ''}`}
              >
                <div className="subscription-card-badge">Plan</div>
                <div className="subscription-card-value">{subscription.value}</div>
              </div>
            );
          })}
        </div>
      </div>

      <button
        onClick={handleCreateAccount}
        disabled={loading || !email.trim() || selectedSubscriptionId === null}
      >
        {loading ? 'Creating...' : 'Create Account'}
      </button>
    </SectionWrapper>
  );
}
