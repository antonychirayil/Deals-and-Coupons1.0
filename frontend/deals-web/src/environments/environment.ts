// Used by "ng build" (production, e.g. the Docker image).
// "/api" is relative: the browser calls the same address the site came from, and nginx
// forwards it to the gateway (see nginx.conf). The interceptor matches URLs starting with "/api".
export const environment = {
  apiUrl: '/api',
};
