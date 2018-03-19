package com.thinkbiganalytics.nifi.rest.support;

/*-
 * #%L
 * thinkbig-nifi-rest-model
 * %%
 * Copyright (C) 2017 ThinkBig Analytics
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.thinkbiganalytics.nifi.rest.model.NiFiPropertyDescriptor;
import com.thinkbiganalytics.nifi.rest.model.NiFiRemoteProcessGroup;
import com.thinkbiganalytics.nifi.rest.model.NifiProperty;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.nifi.web.api.dto.ProcessGroupDTO;
import org.apache.nifi.web.api.dto.RemoteProcessGroupDTO;
import org.apache.nifi.web.api.dto.TemplateDTO;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

//import org.apache.commons.beanutils.PropertyUtils;

public class NifiRemoteProcessGroupUtil {

    /*
        private String targetUri;
    private String targetUris;
    private Boolean targetSecure;
    private String name;
    private String comments;
    private String communicationsTimeout;
    private String yieldDuration;
    private String transportProtocol;
    private String localNetworkInterface;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyUser;
    private String proxyPassword;
     */
    public static List<NiFiPropertyDescriptor> REMOTE_PROCESS_GROUP_PROPERTIES = Lists.newArrayList(propertyDescriptorDTO("targetUris", "URLs", true, false),
                                                                                                    propertyDescriptorDTO("transportProtocol"),
                                                                                                    propertyDescriptorDTO("localNetworkInterface"),
                                                                                                    propertyDescriptorDTO("proxyHost", "HTTP Proxy Server Hostname"),
                                                                                                    propertyDescriptorDTO("proxyPort", "HTTP Proxy Server Port"),
                                                                                                    propertyDescriptorDTO("proxyUser", "HTTP Proxy User"),
                                                                                                    propertyDescriptorDTO("proxyPassword", "HTTP Proxy Password", false, true),
                                                                                                    propertyDescriptorDTO("communicationsTimeout"),
                                                                                                    propertyDescriptorDTO("yieldDuration"));

    private static NiFiPropertyDescriptor propertyDescriptorDTO(String key, String label, boolean required, boolean sensitive) {
        NiFiPropertyDescriptor descriptorDTO = new NiFiPropertyDescriptor();
        descriptorDTO.setName(key);
        descriptorDTO.setDisplayName(label);
        descriptorDTO.setRequired(required);
        descriptorDTO.setSensitive(sensitive);
        return descriptorDTO;
    }

    private static NiFiPropertyDescriptor propertyDescriptorDTO(String key, String label) {
        return propertyDescriptorDTO(key, label, false, false);
    }

    private static NiFiPropertyDescriptor propertyDescriptorDTO(String key) {
        return propertyDescriptorDTO(key, false, false);
    }

    private static NiFiPropertyDescriptor propertyDescriptorDTO(String key, boolean required, boolean sensitive) {
        String label = WordUtils.capitalizeFully(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key), '_');
        label = Arrays.stream(label.split("_")).collect(Collectors.joining(" "));
        return propertyDescriptorDTO(key, label, required, sensitive);
    }

    private static Map<String, NiFiPropertyDescriptor>
        remoteProcessGroupPropertiesMap =
        REMOTE_PROCESS_GROUP_PROPERTIES.stream().collect(Collectors.toMap(NiFiPropertyDescriptor::getName, Function.identity()));

    public static NiFiRemoteProcessGroup toRemoteProcessGroup(RemoteProcessGroupDTO groupDTO) {
        NiFiRemoteProcessGroup remoteProcessGroup = new NiFiRemoteProcessGroup();

        remoteProcessGroup.setId(groupDTO.getId());
        remoteProcessGroup.setName(groupDTO.getName());
        remoteProcessGroup.setActiveRemoteInputPortCount(groupDTO.getActiveRemoteInputPortCount());
        remoteProcessGroup.setActiveRemoteOutputPortCount(groupDTO.getActiveRemoteOutputPortCount());
        remoteProcessGroup.setInactiveRemoteInputPortCount(groupDTO.getInactiveRemoteInputPortCount());
        remoteProcessGroup.setInactiveRemoteOutputPortCount(groupDTO.getInactiveRemoteOutputPortCount());
        remoteProcessGroup.setComments(groupDTO.getComments());
        remoteProcessGroup.setAuthorizationIssues(groupDTO.getAuthorizationIssues());
        //    remoteProcessGroup.setValidationErrors(groupDTO.getValidationErrors());
        remoteProcessGroup.setCommunicationsTimeout(groupDTO.getCommunicationsTimeout());
        remoteProcessGroup.setOutputPortCount(groupDTO.getOutputPortCount());
        remoteProcessGroup.setInputPortCount(groupDTO.getInputPortCount());
        remoteProcessGroup.setTransportProtocol(groupDTO.getTransportProtocol());
        //  remoteProcessGroup.setTargetUris(groupDTO.getTargetUris());
        remoteProcessGroup.setTargetUri(groupDTO.getTargetUri());
        remoteProcessGroup.setProxyHost(groupDTO.getProxyHost());
        remoteProcessGroup.setProxyPort(groupDTO.getProxyPort());
        remoteProcessGroup.setProxyPassword(groupDTO.getProxyPassword());
        remoteProcessGroup.setProxyUser(groupDTO.getProxyUser());
        remoteProcessGroup.setCommunicationsTimeout(groupDTO.getCommunicationsTimeout());
        remoteProcessGroup.setYieldDuration(groupDTO.getYieldDuration());
        remoteProcessGroup.setTargetSecure(groupDTO.isTargetSecure());
        remoteProcessGroup.setParentGroupId(groupDTO.getParentGroupId());

        return remoteProcessGroup;
    }

    /**
     * Return remote process groups for a given template
     * Recursively search the template for all child remote process groups
     */
    public static List<RemoteProcessGroupDTO> remoteProcessGroupDtos(TemplateDTO templateDTO) {
        List<RemoteProcessGroupDTO> groups = templateDTO.getSnippet().getRemoteProcessGroups().stream().collect(Collectors.toList());
        templateDTO.getSnippet().getProcessGroups().stream().forEach(groupDTO -> groups.addAll(remoteProcessGroups(groupDTO)));
        return groups;

    }

    public static List<NiFiRemoteProcessGroup> niFiRemoteProcessGroup(TemplateDTO templateDTO) {
        List<RemoteProcessGroupDTO> groups = templateDTO.getSnippet().getRemoteProcessGroups().stream().collect(Collectors.toList());
        templateDTO.getSnippet().getProcessGroups().stream().forEach(groupDTO -> groups.addAll(remoteProcessGroups(groupDTO)));
        return groups.stream().map(group -> toRemoteProcessGroup(group)).collect(Collectors.toList());

    }

    public static List<RemoteProcessGroupDTO> remoteProcessGroups(ProcessGroupDTO groupDTO) {
        List<RemoteProcessGroupDTO> remoteProcessGroupDTOS = new ArrayList<>();
        remoteProcessGroupDTOS.addAll(groupDTO.getContents().getRemoteProcessGroups());
        groupDTO.getContents().getProcessGroups().forEach(groupDTO1 -> remoteProcessGroupDTOS.addAll(remoteProcessGroups(groupDTO1)));
        return remoteProcessGroupDTOS;
    }


    @Nullable
    private static String getPropertyAsString(RemoteProcessGroupDTO remoteProcessGroupDTO, PropertyDescriptor propertyDescriptor) {
        Object value = null;
        try {
                value = PropertyUtils.getProperty(remoteProcessGroupDTO, propertyDescriptor.getName());
        } catch (Exception e) {
            //TODO LOG
        }
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }

    }

    /**
     * Return the Remote Process groups as lists of properties related to each group
     */
    public static List<NifiProperty> remoteProcessGroupProperties(TemplateDTO templateDTO) {
        return remoteProcessGroupDtos(templateDTO).stream().flatMap(remoteProcessGroupDTO -> remoteProcessGroupProperties(remoteProcessGroupDTO).stream()).collect(Collectors.toList());
    }

    public static List<NifiProperty> remoteProcessGroupProperties(RemoteProcessGroupDTO remoteProcessGroupDTO) {
        List<NifiProperty> list = Arrays.stream(BeanUtils.getPropertyDescriptors(RemoteProcessGroupDTO.class))
            .filter(propertyDescriptor -> remoteProcessGroupPropertiesMap.containsKey(propertyDescriptor.getName()))
            .map(propertyDescriptor -> {
                NifiProperty
                    property =
                    new NifiProperty(remoteProcessGroupDTO.getParentGroupId(), remoteProcessGroupDTO.getId(), propertyDescriptor.getName(),
                                     getPropertyAsString(remoteProcessGroupDTO, propertyDescriptor));
                property.setProcessorType("RemoteProcessGroup");
                property.setProcessGroupName(remoteProcessGroupDTO.getParentGroupId());
                property.setProcessorName(remoteProcessGroupDTO.getName());
                property.setProcessGroupName("NiFi Flow");
                NiFiPropertyDescriptor propertyDescriptorDTO = remoteProcessGroupPropertiesMap.get(propertyDescriptor.getName());
                property.setPropertyDescriptor(propertyDescriptorDTO);
                return property;


            }).collect(Collectors.toList());

        return list;
    }

}
