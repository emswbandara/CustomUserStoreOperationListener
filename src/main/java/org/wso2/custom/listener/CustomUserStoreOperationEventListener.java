package org.wso2.custom.listener;

import org.apache.commons.lang.ArrayUtils;
import org.wso2.carbon.identity.core.AbstractIdentityUserOperationEventListener;
import org.wso2.carbon.identity.core.util.IdentityCoreConstants;
import org.wso2.carbon.tenant.mgt.util.TenantMgtUtil;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.custom.listener.internal.CustomUserStoreOperationListenerServiceComponent;

import static org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_ID;

public class CustomUserStoreOperationEventListener extends AbstractIdentityUserOperationEventListener {


    public CustomUserStoreOperationEventListener() {

        super();
    }

    @Override
    public int getExecutionOrderId() {

        int orderId = getOrderId();
        if (orderId != IdentityCoreConstants.EVENT_LISTENER_ORDER_ID) {
            return orderId;
        }
        return 93;
    }

    @Override
    public boolean doPostAddRole(String roleName, String[] userList, Permission[] permissions,
                                 UserStoreManager userStoreManager) throws UserStoreException {

        // roles should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (userStoreManager.getTenantId() != SUPER_TENANT_ID) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    UserStoreManager tenantUserStoreManager = TenantMgtUtil.getUserStoreManager(tenant, tenantId);
                    tenantUserStoreManager.addRole(roleName, null, permissions);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred while adding role in tenants", e);
        }
        return true;
    }

    @Override
    public boolean doPostUpdateRoleName(String roleName, String newRoleName, UserStoreManager userStoreManager)
            throws UserStoreException {

        // roles should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (userStoreManager.getTenantId() != SUPER_TENANT_ID) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    UserStoreManager tenantUserStoreManager = TenantMgtUtil.getUserStoreManager(tenant, tenantId);
                    tenantUserStoreManager.updateRoleName(roleName, newRoleName);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred while updating role in tenants", e);
        }
        return true;
    }

    public boolean doPostDeleteRole(String roleName, UserStoreManager userStoreManager) throws UserStoreException {

        // roles should be inherited from super-tenant. Tenant specific roles will not be reflected in other tenants.
        if (userStoreManager.getTenantId() != SUPER_TENANT_ID) {
            return true;
        }

        try {
            Tenant[] tenants = getAllTenants();
            if (!ArrayUtils.isEmpty(tenants)) {
                for (Tenant tenant : tenants) {
                    int tenantId = tenant.getId();
                    UserStoreManager tenantUserStoreManager = TenantMgtUtil.getUserStoreManager(tenant, tenantId);
                    tenantUserStoreManager.deleteRole(roleName);
                }
            }
        } catch (Exception e) {
            throw new UserStoreException("Error occurred while updating role in tenants", e);
        }
        return true;
    }

    /**
     * Get the list of the tenants
     *
     * @return List<TenantInfoBean>
     * @throws Exception UserStorException
     */
    public static Tenant[] getAllTenants() throws UserStoreException {

        if(CustomUserStoreOperationListenerServiceComponent.getRealmService() == null) {
            return null;
        }
        org.wso2.carbon.user.core.tenant.TenantManager tenantManager =
                CustomUserStoreOperationListenerServiceComponent.getRealmService().getTenantManager();
        Tenant[] tenants;
        try {
            tenants = (Tenant[]) tenantManager.getAllTenants();

        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            String msg = "Error in retrieving the tenant information.";
            throw new UserStoreException(msg, e);
        }
        return tenants;
    }
}
