package com.thinkbiganalytics.nifi.processors.ranger.authorization.service.util;

import com.thinkbiganalytics.datalake.authorization.model.HDFSPolicy;
import com.thinkbiganalytics.datalake.authorization.model.HadoopAuthorizationPolicy;
import com.thinkbiganalytics.datalake.authorization.model.SearchPolicy;
import com.thinkbiganalytics.datalake.authorization.rest.client.RangerRestClient;
import com.thinkbiganalytics.datalake.authorization.rest.model.RangerCreatePolicy;
import com.thinkbiganalytics.datalake.authorization.rest.model.RangerUpdatePolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Shashi Vishwakarma on 10/05/16.
 */

public class RangerUtil 
{

	public static Logger logger = LoggerFactory.getLogger(RangerUtil.class);

	private static String HDFS_REPOSITORY_TYPE ="hdfs";
	private static String HIVE_REPOSITORY_TYPE ="hive";
	private static String IsEnable ="true";
	private static String IsRecursive ="true";
	private static String IsAuditable = "true";
	private static String modelDBPath ="/model.db/";
	private static String appBasePath ="/app/warehouse/";
	private static String etlBasePath ="/etl/";
	private static String archiveBasePath ="/archive/";
	private static String NIFI = "nifi_";
	private static String HIVE_COLUMN_PERMISSION="*";
	private static String REPOSITORY_TYPE = "repositoryType";
	private static String POLICY_NAME="policyName";



	@SuppressWarnings({ "unchecked", "static-access" })
	public RangerCreatePolicy getHDFSCreatePolicy(String group_list, String permission_level, String category_name, String feed_name, String hdfs_permission_list, String hdfs_reposiroty_name)
	{
		logger.info("Creating Ranger HDFS Policy");

		/**
		 * Create HDFS Policy 
		 */

		RangerCreatePolicy rangerCreatePolicy = new RangerCreatePolicy();

		String ranger_hdfs_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ HDFS_REPOSITORY_TYPE;
		String resource_name = constructResourceforPermissionHDFS(category_name,feed_name ,permission_level);
		String description = "Ranger policy created for group list " +group_list+ " for resource " + feed_name ;

		/**
		 * Convert String Values int0 Array List
		 */

		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList Grouparraylist = new ArrayList(groupList);

		List<String> hdfs_permission_list_LIST = Arrays.asList(hdfs_permission_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList HDFSPermissionarraylist = new ArrayList(hdfs_permission_list_LIST);

		rangerCreatePolicy.setPolicyName(ranger_hdfs_policy_name);
		rangerCreatePolicy.setResourceName(resource_name);
		rangerCreatePolicy.setDescription(description);
		rangerCreatePolicy.setRepositoryName(hdfs_reposiroty_name);
		rangerCreatePolicy.setRepositoryType(HDFS_REPOSITORY_TYPE);
		rangerCreatePolicy.setIsEnabled(IsEnable);
		rangerCreatePolicy.setIsRecursive(IsRecursive);
		rangerCreatePolicy.setIsAuditEnabled(IsAuditable);
		rangerCreatePolicy.setPermMapList( Grouparraylist, HDFSPermissionarraylist);

		return rangerCreatePolicy;

	}

	private String constructResourceforPermissionHDFS(String category_name, String feed_name , String permission_level) {
		// TODO Auto-generated method stub
		String final_resource_path="";

		//Check level at which permission needs to be defined.
		if (permission_level.equalsIgnoreCase("category"))
		{
			String modeldb = modelDBPath + category_name;
			String appPath = appBasePath + category_name;
			String etlPath = etlBasePath + category_name;
			String archivePath = archiveBasePath + category_name;
			final_resource_path = modeldb +" , "+ appPath + " , "+ etlPath + "," + archivePath; 
		}
		else
		{
			String modeldb = modelDBPath + category_name + "/" +feed_name;
			String appPath = appBasePath + category_name + "/" + feed_name;
			String etlPath = etlBasePath + category_name + "/" + feed_name ;
			String archivePath = archiveBasePath + category_name + "/" +feed_name;
			final_resource_path = modeldb +" , "+ appPath + " , "+ etlPath + "," + archivePath; 
		}
		return final_resource_path;
	}

	public SearchPolicy getSearchPolicyJson(String policyName, String repositoryType)
	{

		logger.info("Start of Search Policy  - Check If Policy Exits");
		SearchPolicy search = new SearchPolicy();
		search.setRepositoryType(repositoryType);
		search.setPolicyName(policyName);
		search.setEnabled(true);
		logger.info("End of Search Policy  - Check If Policy Exits");

		return search;
	}


	public RangerCreatePolicy getHIVECreatePolicy(String group_list, String permission_level, String category_name,
			String feed_name, String hive_permission_list, String hive_reposiroty_name) {

		logger.info("Creating Ranger Hive Policy");

		/**
		 * Creating Hive Policy
		 */

		RangerCreatePolicy rangerCreatePolicy = new RangerCreatePolicy();

		String ranger_hive_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ HDFS_REPOSITORY_TYPE;
		String hive_description = "Ranger policy created for group list " +group_list.toString()+ " for resource " + category_name.toString() ;

		/**
		 * Convert String values to List
		 */
		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList groupListHive = new ArrayList(groupList);

		List<String> hive_permission_list_LIST = Arrays.asList(hive_permission_list.split("\\s*,\\s*"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayList permListHive = new ArrayList(hive_permission_list_LIST);


		/**
		 * Construct create policy object
		 */

		rangerCreatePolicy.setPolicyName(ranger_hive_policy_name);
		rangerCreatePolicy.setDatabases(category_name);
		rangerCreatePolicy.setTables(feed_name);
		rangerCreatePolicy.setColumns(HIVE_COLUMN_PERMISSION);
		rangerCreatePolicy.setUdfs("");
		rangerCreatePolicy.setDescription(hive_description);
		rangerCreatePolicy.setRepositoryName(hive_reposiroty_name);
		rangerCreatePolicy.setRepositoryType(HIVE_REPOSITORY_TYPE);
		rangerCreatePolicy.setIsAuditEnabled(IsAuditable);
		rangerCreatePolicy.setIsEnabled(IsEnable);
		rangerCreatePolicy.setPermMapList(groupListHive, permListHive);

		return rangerCreatePolicy;




		/*		HivePolicy hivePolicy = new HivePolicy();

		//Add remove users/groups/permissions to test createPolicy function
		//Passing permission list is mandatory if 'usersList' or 'groupList' is passed otherwise createPolicy() will fail


		//Covert group string to array list.
		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList groupListHive = new ArrayList(groupList);

		//Covert array string to array list.
		List<String> hive_permission_list_LIST = Arrays.asList(hive_permission_list.split("\\s*,\\s*"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayList permListHive = new ArrayList(hive_permission_list_LIST);

		String ranger_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level;
		String tablePermission = constructResourceforPermissionHIVE(category_name , feed_name , permission_level);
		String description = "Ranger policy created for group list " +group_list+ " for resource " + feed_name ;

		hivePolicy.setPolicyName(ranger_policy_name);
		hivePolicy.setDatabases(category_name);
		hivePolicy.setTables(tablePermission);
		hivePolicy.setColumns(HIVE_COLUMN_PERMISSION);
		hivePolicy.setUdfs("");
		hivePolicy.setDescription(description);
		hivePolicy.setRepositoryName(hive_reposiroty_name);
		hivePolicy.setRepositoryType(HIVE_REPOSITORY_TYPE);
		hivePolicy.setIsEnabled(IsEnable);
		hivePolicy.setIsAuditEnabled(IsAuditable);
		hivePolicy.setGroupList(groupListHive);
		hivePolicy.setPermList(permListHive);

		logger.info("End of getHIVECreatePolicyJson  - Contruct Hive Policy");

		return hivePolicy;
		 */	}

	private String constructResourceforPermissionHIVE(String category_name, String feed_name, String permission_level) {
		// TODO Auto-generated method stub
		String final_table_PermissionList ="";
		if (permission_level.equalsIgnoreCase("category"))
		{
			//Give all permission att database level
			final_table_PermissionList = "*";
		}
		else
		{
			//Give Permissionto Feed Level
			final_table_PermissionList = feed_name + "," + feed_name + "_feed" + "," +
					feed_name + "_invalid" + "," +
					feed_name + "_profile"	+ "," +
					feed_name + "_valid" ;
		}

		return final_table_PermissionList;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public RangerUpdatePolicy updateHDFSPolicy( String group_list, String permission_level, String category_name, String feed_name, String hdfs_permission_list, String hdfs_reposiroty_name)
	{
		logger.info("Updating HDFS Policy");

		RangerUpdatePolicy rangerUpdatePolicy = new RangerUpdatePolicy();


		String ranger_hdfs_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ HDFS_REPOSITORY_TYPE;
		String resource_name = constructResourceforPermissionHDFS(category_name,feed_name ,permission_level);
		String description = "Ranger policy updated  for group list " +group_list+ " for resource " + feed_name ;

		/**
		 * Convert String Values into Array List
		 */

		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList Grouparraylist = new ArrayList(groupList);

		List<String> hdfs_permission_list_LIST = Arrays.asList(hdfs_permission_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList HDFSPermissionarraylist = new ArrayList(hdfs_permission_list_LIST);


		rangerUpdatePolicy.setPolicyName(ranger_hdfs_policy_name);
		rangerUpdatePolicy.setResourceName(resource_name);
		rangerUpdatePolicy.setDescription(description);
		rangerUpdatePolicy.setRepositoryName(hdfs_reposiroty_name);
		rangerUpdatePolicy.setRepositoryType(HDFS_REPOSITORY_TYPE);
		rangerUpdatePolicy.setIsEnabled(IsEnable);
		rangerUpdatePolicy.setIsRecursive(IsRecursive);
		rangerUpdatePolicy.setIsAuditEnabled(IsAuditable);
		rangerUpdatePolicy.setPermMapList( Grouparraylist, HDFSPermissionarraylist);

		return rangerUpdatePolicy;

		/*
		HDFSPolicy policy = new HDFSPolicy();

		//Add remove users/groups/permissions to test createPolicy function
		//Passing permission list is mandatory if 'usersList' or 'groupList' otherwise createPolicy() will fail

		//Covert group string to array list.
		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList Grouparraylist = new ArrayList(groupList);

		//Covert array string to array list.
		List<String> hdfs_permission_list_LIST = Arrays.asList(hdfs_permission_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList HDFSPermissionarraylist = new ArrayList(hdfs_permission_list_LIST);

		String ranger_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level;
		String resource_name = constructResourceforPermissionHDFS(category_name,feed_name ,permission_level);
		String description = "Ranger policy updated for group list " +group_list+ " for resource " + resource_name ;

		policy.setPolicyName(ranger_policy_name);
		policy.setResourceName(resource_name);
		policy.setDescription(description);
		policy.setRepositoryName(hdfs_reposiroty_name);
		policy.setRepositorytype(HDFS_REPOSITORY_TYPE);
		policy.setIsEnabled(IsEnable);
		policy.setIsRecursive(IsRecursive);
		policy.setIsAuditEnabled(IsAuditable);
		policy.setGroups(Grouparraylist);
		policy.setPermissions(HDFSPermissionarraylist);

		logger.info("End of getHDFSUpdatePolicyJson");
		return policy;
		 */
	}

	public RangerUpdatePolicy updateHivePolicy(String group_list, String permission_level, String category_name,
			String feed_name, String hive_permission_list, String hive_reposiroty_name)
	{
		// TODO Auto-generated method stub

		logger.info("Updating Hive Policy");

		RangerUpdatePolicy rangerUpdatePolicy = new RangerUpdatePolicy();

		String ranger_hive_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ HIVE_REPOSITORY_TYPE;
		String hive_description = "Ranger policy updated for group list " +group_list.toString()+ " for resource " + category_name.toString() ;

		/**
		 * Convert String values to List
		 */
		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList groupListHive = new ArrayList(groupList);

		List<String> hive_permission_list_LIST = Arrays.asList(hive_permission_list.split("\\s*,\\s*"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayList permListHive = new ArrayList(hive_permission_list_LIST);


		/**
		 * Construct create policy object
		 */

		rangerUpdatePolicy.setPolicyName(ranger_hive_policy_name);
		rangerUpdatePolicy.setDatabases(category_name);
		rangerUpdatePolicy.setTables(feed_name);
		rangerUpdatePolicy.setColumns(HIVE_COLUMN_PERMISSION);
		rangerUpdatePolicy.setUdfs("");
		rangerUpdatePolicy.setDescription(hive_description);
		rangerUpdatePolicy.setRepositoryName(hive_reposiroty_name);
		rangerUpdatePolicy.setRepositoryType(HIVE_REPOSITORY_TYPE);
		rangerUpdatePolicy.setIsAuditEnabled(IsAuditable);
		rangerUpdatePolicy.setIsEnabled(IsEnable);
		rangerUpdatePolicy.setPermMapList(groupListHive, permListHive);

		return rangerUpdatePolicy;

		/*
		HivePolicy hivePolicy = new HivePolicy();

		//Add remove users/groups/permissions to test createPolicy function
		//Passing permission list is mandatory if 'usersList' or 'groupList' is passed otherwise createPolicy() will fail


		//Covert group string to array list.
		List<String> groupList = Arrays.asList(group_list.split("\\s*,\\s*"));
		@SuppressWarnings("rawtypes")
		ArrayList groupListHive = new ArrayList(groupList);

		//Covert array string to array list.
		List<String> hive_permission_list_LIST = Arrays.asList(hive_permission_list.split("\\s*,\\s*"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayList permListHive = new ArrayList(hive_permission_list_LIST);

		String ranger_policy_name = NIFI+category_name+"_"+feed_name+"_"+permission_level;
		String tablePermission = constructResourceforPermissionHIVE(category_name , feed_name , permission_level);
		String description = "Ranger policy updated for group list " +group_list+ " for resource " + feed_name ;

		hivePolicy.setPolicyName(ranger_policy_name);
		hivePolicy.setDatabases(category_name);
		hivePolicy.setTables(tablePermission);
		hivePolicy.setColumns(HIVE_COLUMN_PERMISSION);
		hivePolicy.setUdfs("");
		hivePolicy.setDescription(description);
		hivePolicy.setRepositoryName(hive_reposiroty_name);
		hivePolicy.setRepositoryType(HIVE_REPOSITORY_TYPE);
		hivePolicy.setIsEnabled(IsEnable);
		hivePolicy.setIsAuditEnabled(IsAuditable);
		hivePolicy.setGroupList(groupListHive);
		hivePolicy.setPermList(permListHive);

		logger.info("End of getHIVEUpdatePolicyJson  - Update Hive Policy");

		return hivePolicy;
		 */
	}
	public boolean checkIfPolicyExists(RangerRestClient rangerClientObject, String category_name, String feed_name, String permission_level, String repositoryType)  throws
	Exception {

		logger.info("Check if Policy Exists.");

		String policy_serach_criteria  = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ repositoryType;;
		Map<String,Object> searchHDFSCriteria = new HashMap<>();
		searchHDFSCriteria.put(POLICY_NAME,policy_serach_criteria);
		searchHDFSCriteria.put(REPOSITORY_TYPE, repositoryType);
		List<HadoopAuthorizationPolicy>  hadoopPolicyList = rangerClientObject.searchPolicies(searchHDFSCriteria);  

		if (hadoopPolicyList.size() == 0)
		{
			return false;
		}
		else
		{
			if(hadoopPolicyList.size() > 1)
			{
				throw new Exception("Unable to find unique policy.");
			}
			else
			{

				return true;
			}

			/*	HadoopAuthorizationPolicy  hadoopPolicy = hadoopPolicyList.get(1);
			if(hadoopPolicy.getPolicyCount() > 0)
			{
				return true;
			}
			else
			{
				return false;
			}*/
		}

		/*String policy_serach_criteria  = NIFI+category_name+"_"+feed_name+"_"+permission_level;
		SearchPolicy  searchPolicyObj = getSearchPolicyJson(policy_serach_criteria,repositoryType);
		String searchResponse = rangerClientObject.searchPolicies(searchPolicyObj.searchCriteria());

		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(searchResponse);

			if (json.containsKey(TOTAL_COUNT))
			{
				int totalPolicyCount =  Integer.parseInt(json.get(TOTAL_COUNT).toString());

				if (totalPolicyCount > 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Unable to parse search policy Json " + e.getMessage());
		}
		 */		

	}

	public int getIdForExistingPolicy(RangerRestClient rangerClientObject, String category_name, String feed_name, String permission_level, String repositoryType) throws Exception
	{

		int policyId = 0 ;
		String policy_serach_criteria  = NIFI+category_name+"_"+feed_name+"_"+permission_level+"_"+ repositoryType;
		Map<String,Object> searchHDFSCriteria = new HashMap<>();
		searchHDFSCriteria.put(POLICY_NAME,policy_serach_criteria);
		searchHDFSCriteria.put(REPOSITORY_TYPE, repositoryType);
		List<HadoopAuthorizationPolicy>  hadoopPolicyList = rangerClientObject.searchPolicies(searchHDFSCriteria);  //this.searchPolicy(searchHDFSCriteria);

		/*if (hadoopPolicyList.size() > 1)
		{
			throw new UnsupportedOperationException("Unable to get ID for Ranger Policy");
		}
		else
		{
			HadoopAuthorizationPolicy  hadoopPolicy = hadoopPolicyList.get(1);
			policyId =hadoopPolicy.getPolicyId();

		}*/

		if (hadoopPolicyList.size() == 0)
		{
			throw new UnsupportedOperationException("Unable to get ID for Ranger Policy");	
		}
		else
		{
			if(hadoopPolicyList.size() > 1)
			{
				throw new Exception("Unable to find unique policy.");
			}
			else
			{

				for(HadoopAuthorizationPolicy hadoopPolicy : hadoopPolicyList)
				{
					policyId = hadoopPolicy.getPolicyId();
					logger.info("Got Policy ID  - " +policyId);
				}
			}
		}

		/*String policy_serach_criteria  = NIFI+category_name+"_"+feed_name+"_"+permission_level;
		SearchPolicy  searchPolicyObj = getSearchPolicyJson(policy_serach_criteria,repositoryType);
		String searchResponse = rangerClientObject.searchPolicies(searchPolicyObj.searchCriteria());

		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(searchResponse);

			if (json.containsKey(VXPOLICIES))
			{
				JSONArray policyJsonArray = (JSONArray) json.get(VXPOLICIES);
				JSONObject firstPolicy =  (JSONObject) policyJsonArray.get(0);
				policyId =  Integer.parseInt(firstPolicy.get("id").toString());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Unable to get policy Id  " + e.getMessage());
			throw new ParseException(policyId, "Unable to get ranger policy ID "  + e);
		}

		logger.info("Got Policy Id -" + policyId);

		 */	
		return policyId;

	}
}

