package org.wso2.custom.listener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.user.core.AuthorizationManager;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractAuthorizationManagerListener;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.custom.listener.internal.CustomUserStoreOperationListenerServiceComponent;

import static org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_ID;
import static org.wso2.custom.listener.CustomUserStoreOperationEventListener.getAllTenants;

public class PermissionAuthorizationListener extends AbstractAuthorizationManagerListener {

    @Override
    public int getExecutionOrderId() {

        return 6;
    }

    public boolean authorizeRole(String roleName, String resourceId, String action,
                                 AuthorizationManager authorizationManager) throws UserStoreException {

        // role operations should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (authorizationManager.getTenantId() != SUPER_TENANT_ID && StringUtils.equals(roleName, "system/wso2.anonymous.role")) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    AuthorizationManager authManager = getAuthManager(tenantId);
                    authManager.authorizeRole(roleName, resourceId, action);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred authorizing role.", e);
        }
        return true;
    }

    public boolean clearRoleActionOnAllResources(String roleName, String action,
                                                 AuthorizationManager authorizationManager) throws
            UserStoreException {
        // role operations should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (authorizationManager.getTenantId() != SUPER_TENANT_ID) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    AuthorizationManager authManager = getAuthManager(tenantId);
                    authManager.clearRoleActionOnAllResources(roleName, action);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred authorizing role.", e);
        }
        return true;
    }

    public boolean clearRoleAuthorization(String roleName, String resourceId, String action,
                                          AuthorizationManager authorizationManager) throws UserStoreException {
        // role operations should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (authorizationManager.getTenantId() != SUPER_TENANT_ID) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    AuthorizationManager authManager = getAuthManager(tenantId);
                    authManager.clearRoleAuthorization(roleName, resourceId, action);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred authorizing role.", e);
        }
        return true;
    }


    private AuthorizationManager getAuthManager(int tenantId) throws Exception {

        try {
            UserRealm realm = (UserRealm) CustomUserStoreOperationListenerServiceComponent.
                    getRealmService().getTenantUserRealm(tenantId);
            AuthorizationManager authMan = realm.getAuthorizationManager();
            return authMan;

        } catch (UserStoreException e) {
            String msg = "Error in authorizing the admin role.";
            throw new Exception(msg, e);
        }
    }
}
