/*     */ package flex.messaging.config;
/*     */ 
/*     */ public abstract interface ConfigurationConstants
/*     */ {
/*     */   public static final String CONTEXT_PATH_TOKEN = "{context.root}";
/*     */   public static final String CONTEXT_PATH_ALT_TOKEN = "{context-root}";
/*     */   public static final String SLASH_CONTEXT_PATH_TOKEN = "/{context.root}";
/*     */   public static final String EMPTY_STRING = "";
/*     */   public static final String SERVER_NAME_TOKEN = "{server.name}";
/*     */   public static final String SERVER_PORT_TOKEN = "{server.port}";
/*     */   public static final String SERVICES_CONFIG_ELEMENT = "services-config";
/*     */   public static final String SERVICES_ELEMENT = "services";
/*     */   public static final String SERVICE_ELEMENT = "service";
/*     */   public static final String SERVICE_INCLUDE_ELEMENT = "service-include";
/*     */   public static final String SRC_ATTR = "file-path";
/*     */   public static final String ID_ATTR = "id";
/*     */   public static final String CLASS_ATTR = "class";
/*     */   public static final String PER_CLIENT_AUTH = "per-client-authentication";
/*     */   public static final String MESSAGE_TYPES_ATTR = "messageTypes";
/*     */   public static final String PROPERTIES_ELEMENT = "properties";
/*     */   public static final String METADATA_ELEMENT = "metadata";
/*     */   public static final String ADAPTERS_ELEMENT = "adapters";
/*     */   public static final String ADAPTER_DEFINITION_ELEMENT = "adapter-definition";
/*     */   public static final String DEFAULT_ATTR = "default";
/*     */   public static final String DEFAULT_CHANNELS_ELEMENT = "default-channels";
/*     */   public static final String CHANNEL_ELEMENT = "channel";
/*     */   public static final String REF_ATTR = "ref";
/*     */   public static final String DEFAULT_SECURITY_CONSTRAINT_ELEMENT = "default-security-constraint";
/*     */   public static final String DESTINATION_ELEMENT = "destination";
/*     */   public static final String DESTINATION_INCLUDE_ELEMENT = "destination-include";
/*     */   public static final String ADAPTER_ELEMENT = "adapter";
/*     */   public static final String ADAPTER_ATTR = "adapter";
/*     */   public static final String CHANNELS_ATTR = "channels";
/*     */   public static final String SECURITY_CONSTRAINT_ELEMENT = "security-constraint";
/*     */   public static final String SECURITY_CONSTRAINT_ATTR = "security-constraint";
/*     */   public static final String SECURITY_ELEMENT = "security";
/*     */   public static final String SECURITY_CONSTRAINT_DEFINITION_ELEMENT = "security-constraint";
/*     */   public static final String AUTH_METHOD_ELEMENT = "auth-method";
/*     */   public static final String ROLES_ELEMENT = "roles";
/*     */   public static final String ROLE_ELEMENT = "role";
/*     */   public static final String LOGIN_COMMAND_ELEMENT = "login-command";
/*     */   public static final String SERVER_ATTR = "server";
/*     */   public static final String SERVERS_ELEMENT = "servers";
/*     */   public static final String SERVER_ELEMENT = "server";
/*     */   public static final String IP_ADDRESS_PATTERN = "ip-address-pattern";
/*     */   public static final String CHANNELS_ELEMENT = "channels";
/*     */   public static final String CHANNEL_DEFINITION_ELEMENT = "channel-definition";
/*     */   public static final String REMOTE_ATTR = "remote";
/*     */   public static final String ENDPOINT_ELEMENT = "endpoint";
/*     */   public static final String URI_ATTR = "uri";
/*     */   public static final String URL_ATTR = "url";
/*     */   public static final String POLLING_ENABLED_ELEMENT = "polling-enabled";
/*     */   public static final String POLLING_INTERVAL_MILLIS_ELEMENT = "polling-interval-millis";
/*     */   public static final String PIGGYBACKING_ENABLED_ELEMENT = "piggybacking-enabled";
/*     */   public static final String LOGIN_AFTER_DISCONNECT_ELEMENT = "login-after-disconnect";
/*     */   public static final String RECORD_MESSAGE_SIZES_ELEMENT = "record-message-sizes";
/*     */   public static final String RECORD_MESSAGE_TIMES_ELEMENT = "record-message-times";
/*     */   public static final String SERIALIZATION_ELEMENT = "serialization";
/*     */   public static final String ENABLE_SMALL_MESSAGES_ELEMENT = "enable-small-messages";
/*     */   public static final String POLLING_INTERVAL_SECONDS_ELEMENT = "polling-interval-seconds";
/*     */   public static final String CONNECT_TIMEOUT_SECONDS_ELEMENT = "connect-timeout-seconds";
/*     */   public static final String CLUSTERS_ELEMENT = "clusters";
/*     */   public static final String CLUSTER_DEFINITION_ELEMENT = "cluster";
/*     */   public static final String CLUSTER_PROPERTIES_ATTR = "properties";
/*     */   public static final String LOGGING_ELEMENT = "logging";
/*     */   public static final String TARGET_ELEMENT = "target";
/*     */   public static final String FILTERS_ELEMENT = "filters";
/*     */   public static final String PATTERN_ELEMENT = "pattern";
/*     */   public static final String LEVEL_ATTR = "level";
/*     */   public static final String SYSTEM_ELEMENT = "system";
/*     */   public static final String LOCALE_ELEMENT = "locale";
/*     */   public static final String MANAGEABLE_ELEMENT = "manageable";
/*     */   public static final String DEFAULT_LOCALE_ELEMENT = "default-locale";
/*     */   public static final String REDEPLOY_ELEMENT = "redeploy";
/*     */   public static final String ENABLED_ELEMENT = "enabled";
/*     */   public static final String WATCH_INTERVAL_ELEMENT = "watch-interval";
/*     */   public static final String WATCH_FILE_ELEMENT = "watch-file";
/*     */   public static final String TOUCH_FILE_ELEMENT = "touch-file";
/*     */   public static final String FACTORIES_ELEMENT = "factories";
/*     */   public static final String FACTORY_ELEMENT = "factory";
/*     */   public static final String FLEX_CLIENT_ELEMENT = "flex-client";
/*     */   public static final String FLEX_CLIENT_TIMEOUT_MINUTES_ELEMENT = "timeout-minutes";
/* 116 */   public static final String[] SERVICES_CONFIG_CHILDREN = { "services", "security", "servers", "channels", "logging", "system", "clusters", "factories", "flex-client" };
/*     */ 
/* 122 */   public static final String[] SERVICES_CHILDREN = { "service", "service-include", "default-channels" };
/*     */ 
/* 126 */   public static final String[] SERVICE_INCLUDE_CHILDREN = { "file-path" };
/*     */ 
/* 130 */   public static final String[] SERVICE_CHILDREN = { "id", "class", "messageTypes", "properties", "adapters", "default-channels", "default-security-constraint", "destination-include", "destination" };
/*     */ 
/* 136 */   public static final String[] SERVICE_REQ_CHILDREN = { "id", "class" };
/*     */ 
/* 140 */   public static final String[] ADAPTER_DEFINITION_CHILDREN = { "id", "class", "default", "properties" };
/*     */ 
/* 144 */   public static final String[] ADAPTER_DEFINITION_REQ_CHILDREN = { "id", "class" };
/*     */ 
/* 148 */   public static final String[] DESTINATION_INCLUDE_CHILDREN = { "file-path" };
/*     */ 
/* 152 */   public static final String[] ADAPTERS_CHILDREN = { "adapter-definition" };
/*     */ 
/* 156 */   public static final String[] DEFAULT_CHANNELS_CHILDREN = { "channel" };
/*     */ 
/* 161 */   public static final String[] SECURITY_CHILDREN = { "security-constraint", "login-command" };
/*     */ 
/* 165 */   public static final String[] EMBEDDED_SECURITY_CHILDREN = { "security-constraint" };
/*     */ 
/* 169 */   public static final String[] SECURITY_CONSTRAINT_DEFINITION_CHILDREN = { "ref", "id", "auth-method", "roles" };
/*     */ 
/* 173 */   public static final String[] ROLES_CHILDREN = { "role" };
/*     */ 
/* 177 */   public static final String[] LOGIN_COMMAND_CHILDREN = { "server", "class", "per-client-authentication" };
/*     */ 
/* 181 */   public static final String[] LOGIN_COMMAND_REQ_CHILDREN = { "server", "class" };
/*     */ 
/* 186 */   public static final String[] SERVERS_CHILDREN = { "server" };
/*     */ 
/* 188 */   public static final String[] SERVER_REQ_CHILDREN = { "id", "class" };
/*     */ 
/* 193 */   public static final String[] CHANNELS_CHILDREN = { "channel-definition" };
/*     */ 
/* 197 */   public static final String[] CHANNEL_DEFINITION_REQ_CHILDREN = { "endpoint", "class", "id" };
/*     */ 
/* 201 */   public static final String[] CHANNEL_DEFINITION_CHILDREN = { "endpoint", "properties", "security", "server", "security-constraint", "class", "id", "remote" };
/*     */ 
/* 206 */   public static final String[] CHANNEL_DEFINITION_SERVER_REQ_CHILDREN = { "ref" };
/*     */ 
/* 210 */   public static final String[] ENDPOINT_CHILDREN = { "uri", "url", "class" };
/*     */ 
/* 214 */   public static final String[] DESTINATION_REQ_CHILDREN = { "id" };
/*     */ 
/* 218 */   public static final String[] DESTINATION_CHILDREN = { "id", "properties", "channels", "security", "adapter", "channels", "adapter", "security-constraint" };
/*     */ 
/* 223 */   public static final String[] DESTINATION_ATTR = { "id", "properties", "channels", "adapter", "channels", "adapter", "security-constraint" };
/*     */ 
/* 228 */   public static final String[] DESTINATION_CHANNEL_REQ_CHILDREN = { "ref" };
/*     */ 
/* 232 */   public static final String[] DESTINATION_CHANNELS_CHILDREN = { "channel" };
/*     */ 
/* 236 */   public static final String[] DESTINATION_ADAPTER_CHILDREN = { "ref" };
/*     */ 
/* 241 */   public static final String[] CLUSTERING_CHILDREN = { "cluster" };
/*     */ 
/* 245 */   public static final String[] CLUSTER_DEFINITION_CHILDREN = { "id", "properties" };
/*     */ 
/* 251 */   public static final String[] LOGGING_CHILDREN = { "properties", "level", "target" };
/*     */ 
/* 255 */   public static final String[] TARGET_CHILDREN = { "class", "level", "properties", "filters" };
/*     */ 
/* 259 */   public static final String[] TARGET_REQ_CHILDREN = { "class" };
/*     */ 
/* 263 */   public static final String[] FILTERS_CHILDREN = { "pattern" };
/*     */ 
/* 269 */   public static final String[] SYSTEM_CHILDREN = { "locale", "redeploy", "manageable" };
/*     */ 
/* 273 */   public static final String[] REDEPLOY_CHILDREN = { "enabled", "watch-interval", "watch-file", "touch-file" };
/*     */ 
/* 277 */   public static final String[] LOCALE_CHILDREN = { "default-locale" };
/*     */ 
/* 282 */   public static final String[] FACTORIES_CHILDREN = { "factory" };
/*     */ 
/* 286 */   public static final String[] FACTORY_REQ_CHILDREN = { "id", "class" };
/*     */   public static final String LIST_DELIMITERS = ",;:";
/*     */   public static final String UNKNOWN_SOURCE_FILE = "uknown file";
/*     */   public static final int PARSER_INIT_ERROR = 10100;
/*     */   public static final int PARSER_INTERNAL_ERROR = 10101;
/*     */   public static final int XML_PARSER_ERROR = 10102;
/*     */   public static final int INVALID_SERVICES_ROOT = 10103;
/*     */   public static final int MISSING_ELEMENT = 10104;
/*     */   public static final int MISSING_ATTRIBUTE = 10105;
/*     */   public static final int UNEXPECTED_ELEMENT = 10106;
/*     */   public static final int UNEXPECTED_ATTRIBUTE = 10107;
/*     */   public static final int TOO_MANY_OCCURRENCES = 10108;
/*     */   public static final int REF_NOT_FOUND = 10109;
/*     */   public static final int INVALID_ID = 10110;
/*     */   public static final int INVALID_ENDPOINT_PORT = 10111;
/*     */   public static final int INVALID_SERVICE_INCLUDE_ROOT = 10112;
/*     */   public static final int DUPLICATE_SERVICE_ERROR = 10113;
/*     */   public static final int CLASS_NOT_SPECIFIED = 10114;
/*     */   public static final int INVALID_DEFAULT_CHANNEL = 10116;
/*     */   public static final int DUPLICATE_DEFAULT_ADAPTER = 10117;
/*     */   public static final int INVALID_DESTINATION_INCLUDE_ROOT = 10118;
/*     */   public static final int INVALID_ID_IN_SERVICE = 10119;
/*     */   public static final int REF_NOT_FOUND_IN_DEST = 10120;
/*     */   public static final int INVALID_REF_IN_DEST = 10121;
/*     */   public static final int DUPLICATE_DESTINATION_ERROR = 10122;
/*     */   public static final int DEST_NEEDS_CHANNEL = 10123;
/*     */   public static final int DEST_NEEDS_ADAPTER = 10127;
/*     */   public static final int REF_NOT_FOUND_IN_CHANNEL = 10132;
/*     */   public static final int UNEXPECTED_TEXT = 11104;
/*     */   public static final int NULL_COMPONENT = 11110;
/*     */   public static final int NULL_COMPONENT_ID = 11111;
/*     */   public static final int DUPLICATE_COMPONENT_ID = 11112;
/*     */   public static final int UNREGISTERED_ADAPTER = 11114;
/*     */   public static final int DUPLICATE_DEST_ID = 11119;
/*     */   public static final int UNDEFINED_CONTEXT_ROOT = 11120;
/*     */   public static final int INVALID_FLEX_CLIENT_TIMEOUT = 11123;
/*     */   public static final int INVALID_SECURITY_CONSTRAINT_REF = 11124;
/*     */   public static final int IRREPLACABLE_TOKEN = 11125;
/*     */   public static final int INVALID_VALUE_FOR_PROPERTY_OF_COMPONENT_WITH_ID = 11126;
/*     */   public static final int DUPLICATE_CHANNEL_ERROR = 11127;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ConfigurationConstants
 * JD-Core Version:    0.6.0
 */