# CustomUserStoreOperationListener
Custom interceptors (UserStoreOperationListener and AuthorizationManagerListener to reflect roles and permission related operations performed to super-tenant domain
other existing tenants.
This provides the ability to create/update super-tenant roles and their permissions in all the existing tenants

**Deploying and Configuring the Custom Interceptors**

Do the following to deploy and enforce the plugin in the WSO2 Identity Server.
1. Compile the code using following command to get the resulting .jar file.

  Command: mvn clean install

2. Copy the .jar file into the <IS_HOME>/repository/components/dropins folder.

3. In the identity.xml file add the following entry under <EventListeners\>

  <EventListener type="org.wso2.carbon.user.core.listener.UserOperationEventListener" 
                name="org.wso2.custom.listener.CustomUserStoreOperationEventListener" 
			          orderId="93" enable="true"/\>
                
	<EventListener type="org.wso2.carbon.user.core.listener.AuthorizationManagerListener" 
                  name="org.wso2.custom.listener.PermissionAuthorizationListener" 
			            orderId="6" enable="true"/\>
                       
 4. Restart the server.                      
