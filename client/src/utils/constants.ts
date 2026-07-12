export const API_ENDPOINTS = {
  CLEAR_DATABASE: '/auth/public/clear-db',
  GET_ALL_SUBSCRIPTION: '/auth/public/get-subscription',
  CREATE_ACCOUNT: '/auth/public/create-account',
  ACTIVATE_ACCOUNT: '/auth/public/activate-account',
  RESEND_ACTIVATION_OTP: '/auth/public/resend-otp-activation',
  GENERATE_TOKEN_OTP: '/auth/public/generate-otp-token',
  VERIFY_TOKEN_OTP: '/auth/public/generate-token',
  GENERATE_RESPONSE_DATA_SLIDING_WINDOW: '/main/private/get-data-slidingwindow',
  GENERATE_RESPONSE_DATA_TOKEN_BUCKET: '/main/private/get-data-tokenbucket',
  USAGE_LIMIT_INFO: '/usage/private/usage-info',
};
