/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/containerpage/Attic/CmsContainerpageService.java,v $
 * Date   : $Date: 2011/02/24 08:06:27 $
 * Version: $Revision: 1.27 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.containerpage;

import org.opencms.ade.containerpage.shared.CmsCntPageData;
import org.opencms.ade.containerpage.shared.CmsContainer;
import org.opencms.ade.containerpage.shared.CmsContainerElement;
import org.opencms.ade.containerpage.shared.CmsContainerElementData;
import org.opencms.ade.containerpage.shared.CmsSubContainer;
import org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.CmsResourceTypeXmlContainerPage;
import org.opencms.flex.CmsFlexController;
import org.opencms.gwt.CmsGwtService;
import org.opencms.gwt.CmsRpcException;
import org.opencms.loader.CmsResourceManager;
import org.opencms.main.CmsException;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.xml.containerpage.CmsADEManager;
import org.opencms.xml.containerpage.CmsADESessionCache;
import org.opencms.xml.containerpage.CmsContainerBean;
import org.opencms.xml.containerpage.CmsContainerElementBean;
import org.opencms.xml.containerpage.CmsContainerPageBean;
import org.opencms.xml.containerpage.CmsSubContainerBean;
import org.opencms.xml.containerpage.CmsXmlContainerPage;
import org.opencms.xml.containerpage.CmsXmlContainerPageFactory;
import org.opencms.xml.containerpage.CmsXmlSubContainer;
import org.opencms.xml.containerpage.CmsXmlSubContainerFactory;
import org.opencms.xml.content.CmsXmlContentProperty;
import org.opencms.xml.content.CmsXmlContentPropertyHelper;
import org.opencms.xml.sitemap.CmsSitemapManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

/**
 * The RPC service used by the container-page editor.<p>
 * 
 * @author Tobias Herrmann
 * 
 * @version $Revision: 1.27 $
 * 
 * @since 8.0.0
 */
public class CmsContainerpageService extends CmsGwtService implements I_CmsContainerpageService {

    /** Static reference to the log. */
    private static final Log LOG = CmsLog.getLog(CmsContainerpageService.class);

    /** Serial version UID. */
    private static final long serialVersionUID = -6188370638303594280L;

    /** The session cache. */
    private CmsADESessionCache m_sessionCache;

    /**
     * Returns a new configured service instance.<p>
     * 
     * @param request the current request
     * 
     * @return a new service instance
     */
    public static CmsContainerpageService newInstance(HttpServletRequest request) {

        CmsContainerpageService srv = new CmsContainerpageService();
        srv.setCms(CmsFlexController.getCmsObject(request));
        srv.setRequest(request);
        return srv;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#addToFavoriteList(java.lang.String)
     */
    public void addToFavoriteList(String clientId) throws CmsRpcException {

        try {
            CmsContainerElementBean element = getCachedElement(clientId);
            List<CmsContainerElementBean> list = OpenCms.getADEManager().getFavoriteList(getCmsObject());
            if (list.contains(element)) {
                list.remove(list.indexOf(element));
            }
            list.add(0, element);
            OpenCms.getADEManager().saveFavoriteList(getCmsObject(), list);
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#addToRecentList(java.lang.String)
     */
    public void addToRecentList(String clientId) throws CmsRpcException {

        try {
            CmsContainerElementBean element = getCachedElement(clientId);
            List<CmsContainerElementBean> list = OpenCms.getADEManager().getRecentList(getCmsObject());
            if (list.contains(element)) {
                list.remove(list.indexOf(element));
            }
            list.add(0, element);
            OpenCms.getADEManager().saveRecentList(getCmsObject(), list);
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#createNewElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public CmsContainerElement createNewElement(String containerpageUri, String clientId, String resourceType)
    throws CmsRpcException {

        CmsContainerElement element = null;
        try {
            CmsResource newResource = OpenCms.getADEManager().createNewElement(
                getCmsObject(),
                containerpageUri,
                getRequest(),
                resourceType);
            CmsContainerElementBean bean = getCachedElement(clientId);
            CmsContainerElementBean newBean = new CmsContainerElementBean(
                newResource.getStructureId(),
                null,
                bean.getProperties(),
                false);
            String newClientId = newBean.getClientId();
            getSessionCache().setCacheContainerElement(newClientId, newBean);
            element = new CmsContainerElement();
            element.setClientId(newClientId);
            element.setSitePath(getCmsObject().getSitePath(newResource));
        } catch (CmsException e) {
            error(e);
        }
        return element;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#deleteElement(java.lang.String)
     */
    public void deleteElement(String clientId) throws CmsRpcException {

        String path = null;
        try {
            CmsResource res = getCmsObject().readResource(OpenCms.getADEManager().convertToServerId(clientId));
            path = getCmsObject().getSitePath(res);
            getCmsObject().lockResource(path);
            getCmsObject().deleteResource(path, CmsResource.DELETE_PRESERVE_SIBLINGS);
        } catch (Exception e) {
            // should never happen
            error(e);
        } finally {
            try {
                if (path != null) {
                    getCmsObject().unlockResource(path);
                }
            } catch (Exception e) {
                // should really never happen
                LOG.debug(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#getElementsData(java.lang.String, java.lang.String, java.util.Collection, java.util.Collection)
     */
    public Map<String, CmsContainerElementData> getElementsData(
        String containerpageUri,
        String reqParams,
        Collection<String> clientIds,
        Collection<CmsContainer> containers) throws CmsRpcException {

        Map<String, CmsContainerElementData> result = null;
        try {
            result = getElements(clientIds, containerpageUri, containers);
        } catch (Throwable e) {
            error(e);
        }
        return result;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#getElementWithProperties(java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.util.Collection)
     */
    public CmsContainerElementData getElementWithProperties(
        String containerpageUri,
        String uriParams,
        String clientId,
        Map<String, String> properties,
        Collection<CmsContainer> containers) throws CmsRpcException {

        CmsContainerElementData element = null;
        try {
            CmsObject cms = getCmsObject();
            CmsElementUtil elemUtil = new CmsElementUtil(cms, uriParams, getRequest(), getResponse());
            CmsUUID serverId = OpenCms.getADEManager().convertToServerId(clientId);
            CmsContainerElementBean elementBean = createElement(serverId, properties);
            getSessionCache().setCacheContainerElement(elementBean.getClientId(), elementBean);
            element = elemUtil.getElementData(elementBean, containers);
        } catch (Throwable e) {
            error(e);
        }
        return element;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#getFavoriteList(java.lang.String, java.util.Collection)
     */
    public List<CmsContainerElementData> getFavoriteList(String containerpageUri, Collection<CmsContainer> containers)
    throws CmsRpcException {

        List<CmsContainerElementData> result = null;
        try {
            result = getListElementsData(
                OpenCms.getADEManager().getFavoriteList(getCmsObject()),
                containerpageUri,
                containers);
        } catch (Throwable e) {
            error(e);
        }
        return result;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#getRecentList(java.lang.String, java.util.Collection)
     */
    public List<CmsContainerElementData> getRecentList(String containerpageUri, Collection<CmsContainer> containers)
    throws CmsRpcException {

        List<CmsContainerElementData> result = null;
        try {
            result = getListElementsData(
                OpenCms.getADEManager().getRecentList(getCmsObject()),
                containerpageUri,
                containers);
        } catch (Throwable e) {
            error(e);
        }
        return result;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#prefetch()
     */
    public CmsCntPageData prefetch() throws CmsRpcException {

        CmsCntPageData data = null;
        CmsObject cms = getCmsObject();

        HttpServletRequest request = getRequest();
        try {
            CmsResource cntPage = getContainerpage(cms);
            String cntPageUri = cms.getSitePath(cntPage);
            data = new CmsCntPageData(
                cms.getSitePath(cntPage),
                getNoEditReason(cms, cntPage),
                CmsRequestUtil.encodeParams(request),
                CmsSitemapManager.PATH_SITEMAP_EDITOR_JSP,
                cntPageUri,
                getNewTypes(cms, request),
                getSessionCache().isToolbarVisible());
        } catch (Throwable e) {
            error(e);
        }
        return data;
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#saveContainerpage(java.lang.String, java.util.List)
     */
    public void saveContainerpage(String containerpageUri, List<CmsContainer> containers) throws CmsRpcException {

        CmsObject cms = getCmsObject();
        try {
            List<CmsContainerBean> containerBeans = new ArrayList<CmsContainerBean>();
            for (CmsContainer container : containers) {
                CmsContainerBean containerBean = getContainerBean(container, containerpageUri);
                containerBeans.add(containerBean);
            }
            CmsContainerPageBean page = new CmsContainerPageBean(cms.getRequestContext().getLocale(), containerBeans);
            cms.lockResourceTemporary(containerpageUri);
            CmsXmlContainerPage xmlCnt = CmsXmlContainerPageFactory.unmarshal(cms, cms.readFile(containerpageUri));
            xmlCnt.save(cms, page);
            cms.unlockResource(containerpageUri);
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#saveFavoriteList(java.util.List)
     */
    public void saveFavoriteList(List<String> clientIds) throws CmsRpcException {

        try {
            OpenCms.getADEManager().saveFavoriteList(getCmsObject(), getCachedElements(clientIds));
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#saveRecentList(java.util.List)
     */
    public void saveRecentList(List<String> clientIds) throws CmsRpcException {

        try {
            OpenCms.getADEManager().saveRecentList(getCmsObject(), getCachedElements(clientIds));
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#saveSubContainer(java.lang.String, java.lang.String, org.opencms.ade.containerpage.shared.CmsSubContainer, java.util.Collection)
     */
    public Map<String, CmsContainerElementData> saveSubContainer(
        String containerpageUri,
        String reqParams,
        CmsSubContainer subContainer,
        Collection<CmsContainer> containers) throws CmsRpcException {

        CmsObject cms = getCmsObject();
        try {
            String resourceName = subContainer.getSitePath();
            if (subContainer.isNew()) {
                CmsResource subContainerResource = OpenCms.getADEManager().createNewElement(
                    getCmsObject(),
                    containerpageUri,
                    getRequest(),
                    CmsResourceTypeXmlContainerPage.SUB_CONTAINER_TYPE_NAME);
                resourceName = cms.getSitePath(subContainerResource);
                subContainer.setSitePath(resourceName);
                subContainer.setClientId(subContainerResource.getStructureId().toString());
            }
            CmsSubContainerBean subContainerBean = getSubContainerBean(subContainer, containerpageUri);
            cms.lockResourceTemporary(resourceName);
            CmsXmlSubContainer xmlSubContainer = CmsXmlSubContainerFactory.unmarshal(cms, cms.readFile(resourceName));
            xmlSubContainer.save(cms, subContainerBean);
            cms.unlockResource(resourceName);
        } catch (Throwable e) {
            error(e);
        }
        Collection<String> ids = new ArrayList<String>();
        ids.add(subContainer.getClientId());
        return getElementsData(containerpageUri, reqParams, ids, containers);
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#setToolbarVisible(boolean)
     */
    public void setToolbarVisible(boolean visible) throws CmsRpcException {

        try {
            getSessionCache().setToolbarVisible(visible);
        } catch (Throwable e) {
            error(e);
        }
    }

    /**
     * @see org.opencms.ade.containerpage.shared.rpc.I_CmsContainerpageService#syncSaveContainerpage(java.lang.String, java.util.List)
     */
    public void syncSaveContainerpage(String containerpageUri, List<CmsContainer> containers) throws CmsRpcException {

        saveContainerpage(containerpageUri, containers);
    }

    /**
     * Creates a new container element from a resource id and a map of properties.<p> 
     * 
     * @param resourceId the resource id 
     * @param properties the map of properties 
     * 
     * @return the new container element bean 
     * 
     * @throws CmsException if something goes wrong 
     */
    private CmsContainerElementBean createElement(CmsUUID resourceId, Map<String, String> properties)
    throws CmsException {

        CmsObject cms = getCmsObject();
        Map<String, CmsXmlContentProperty> propertiesConf = OpenCms.getADEManager().getElementPropertyConfiguration(
            cms,
            cms.readResource(resourceId));

        Map<String, String> changedProps = new HashMap<String, String>();
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String propName = entry.getKey();
                String propType = propertiesConf.get(propName).getPropertyType();
                changedProps.put(
                    propName,
                    CmsXmlContentPropertyHelper.getPropValueIds(cms, propType, properties.get(propName)));
            }
        }
        return new CmsContainerElementBean(resourceId, null, changedProps, false);
    }

    /**
     * Reads the cached element-bean for the given client-side-id from cache.<p>
     * 
     * @param clientId the client-side-id
     * 
     * @return the cached container element bean
     */
    private CmsContainerElementBean getCachedElement(String clientId) {

        String id = clientId;
        CmsContainerElementBean element = null;
        element = getSessionCache().getCacheContainerElement(id);
        if (element != null) {
            return element;
        }
        if (id.contains(CmsADEManager.CLIENT_ID_SEPERATOR)) {
            id = id.substring(0, id.indexOf(CmsADEManager.CLIENT_ID_SEPERATOR));
            element = getSessionCache().getCacheContainerElement(id);
            if (element != null) {
                return element;
            }
        }
        // this is necessary if the element has not been cached yet
        element = new CmsContainerElementBean(OpenCms.getADEManager().convertToServerId(id), null, null, false);
        getSessionCache().setCacheContainerElement(id, element);
        return element;
    }

    /**
     * Returns a list of container elements from a list with client id's.<p>
     * 
     * @param clientIds list of client id's
     * 
     * @return a list of element beans
     */
    private List<CmsContainerElementBean> getCachedElements(List<String> clientIds) {

        List<CmsContainerElementBean> result = new ArrayList<CmsContainerElementBean>();
        for (String id : clientIds) {
            try {
                result.add(getCachedElement(id));
            } catch (CmsIllegalArgumentException e) {
                log(e.getLocalizedMessage(), e);
            }
        }
        return result;
    }

    /**
     * Helper method for converting a CmsContainer to a CmsContainerBean when saving a container page.<p>
     * 
     * @param container the container for which the CmsContainerBean should be created
     * @param containerpageUri the URI of the container page 
     *  
     * @return a container bean
     */
    private CmsContainerBean getContainerBean(CmsContainer container, String containerpageUri) {

        CmsObject cms = getCmsObject();
        CmsADESessionCache cache = getSessionCache();
        List<CmsContainerElementBean> elements = new ArrayList<CmsContainerElementBean>();
        for (CmsContainerElement elementData : container.getElements()) {
            try {
                if (elementData.isNew()) {
                    elementData = createNewElement(
                        containerpageUri,
                        elementData.getClientId(),
                        elementData.getNewType());
                }
                CmsContainerElementBean element = cache.getCacheContainerElement(elementData.getClientId());

                // make sure resource is readable, 
                CmsResource resource = cms.readResource(element.getElementId());

                // check if there is a valid formatter
                int containerWidth = container.getWidth();
                String formatterUri = OpenCms.getADEManager().getFormatterForContainerTypeAndWidth(
                    cms,
                    resource,
                    container.getType(),
                    containerWidth);
                boolean hasValidFormatter = CmsStringUtil.isNotEmptyOrWhitespaceOnly(formatterUri);
                if (hasValidFormatter) {
                    CmsResource formatter = cms.readResource(formatterUri);
                    elements.add(new CmsContainerElementBean(
                        element.getElementId(),
                        formatter.getStructureId(),
                        element.getProperties(),
                        false));
                }
            } catch (Exception e) {
                log(e.getLocalizedMessage(), e);
            }
        }
        CmsContainerBean containerBean = new CmsContainerBean(container.getName(), container.getType(), -1, elements);
        return containerBean;
    }

    /**
     * Returns the requested container-page resource.<p>
     * 
     * @param cms the current cms object
     * 
     * @return the container-page resource
     * 
     * @throws CmsException if the resource could not be read for any reason
     */
    private CmsResource getContainerpage(CmsObject cms) throws CmsException {

        String currentUri = cms.getRequestContext().getUri();
        CmsResource containerPage = cms.readResource(currentUri);
        if (!CmsResourceTypeXmlContainerPage.isContainerPage(containerPage)) {
            // container page is used as template
            String cntPagePath = cms.readPropertyObject(
                containerPage,
                CmsPropertyDefinition.PROPERTY_TEMPLATE_ELEMENTS,
                true).getValue("");
            try {
                containerPage = cms.readResource(cntPagePath);
            } catch (CmsException e) {
                if (!LOG.isDebugEnabled()) {
                    LOG.warn(e.getLocalizedMessage());
                }
                LOG.debug(e.getLocalizedMessage(), e);
            }
        }
        return containerPage;
    }

    /**
     * Returns the data of the given elements.<p>
     * 
     * @param clientIds the list of IDs of the elements to retrieve the data for
     * @param uriParam the current URI
     * @param containers the containers for which the element data should be fetched 
     * 
     * @return the elements data
     * 
     * @throws CmsException if something really bad happens
     */
    private Map<String, CmsContainerElementData> getElements(
        Collection<String> clientIds,
        String uriParam,
        Collection<CmsContainer> containers) throws CmsException {

        CmsObject cms = getCmsObject();
        CmsElementUtil elemUtil = new CmsElementUtil(cms, uriParam, getRequest(), getResponse());
        Map<String, CmsContainerElementData> result = new HashMap<String, CmsContainerElementData>();
        Set<String> ids = new HashSet<String>();
        Iterator<String> it = clientIds.iterator();
        while (it.hasNext()) {
            String elemId = it.next();
            if (ids.contains(elemId)) {
                continue;
            }
            CmsContainerElementBean element = getCachedElement(elemId);
            CmsContainerElementData elementData = elemUtil.getElementData(element, containers);
            result.put(element.getClientId(), elementData);
            if (elementData.isSubContainer()) {
                // this is a sub-container 

                CmsResource elementRes = cms.readResource(element.getElementId());
                CmsXmlSubContainer xmlSubContainer = CmsXmlSubContainerFactory.unmarshal(cms, elementRes, getRequest());
                CmsSubContainerBean subContainer = xmlSubContainer.getSubContainer(
                    cms,
                    cms.getRequestContext().getLocale());

                // adding all sub-items to the elements data
                for (CmsContainerElementBean subElement : subContainer.getElements()) {
                    if (!ids.contains(subElement.getElementId())) {
                        String subId = subElement.getClientId();
                        if (ids.contains(subId)) {
                            continue;
                        }
                        CmsContainerElementData subItemData = elemUtil.getElementData(subElement, containers);
                        ids.add(subId);
                        result.put(subId, subItemData);
                    }
                }
            }
            ids.add(elemId);
        }
        return result;
    }

    /**
     * Returns the data of the given elements.<p>
     * 
     * @param listElements the list of element beans to retrieve the data for
     * @param containerpageUri the current URI
     * @param containers the containers which exist on the container page  
     * 
     * @return the elements data
     * 
     * @throws CmsException if something really bad happens
     */
    private List<CmsContainerElementData> getListElementsData(
        List<CmsContainerElementBean> listElements,
        String containerpageUri,
        Collection<CmsContainer> containers) throws CmsException {

        CmsObject cms = getCmsObject();
        CmsElementUtil elemUtil = new CmsElementUtil(cms, containerpageUri, getRequest(), getResponse());
        List<CmsContainerElementData> result = new ArrayList<CmsContainerElementData>();
        for (CmsContainerElementBean element : listElements) {
            // checking if resource exists
            if (cms.existsResource(element.getElementId(), CmsResourceFilter.ONLY_VISIBLE_NO_DELETED)) {
                CmsContainerElementData elementData = elemUtil.getElementData(element, containers);
                result.add(elementData);
                if (elementData.isSubContainer()) {
                    // this is a sub-container 

                    CmsResource elementRes = cms.readResource(element.getElementId());
                    CmsXmlSubContainer xmlSubContainer = CmsXmlSubContainerFactory.unmarshal(
                        cms,
                        elementRes,
                        getRequest());
                    CmsSubContainerBean subContainer = xmlSubContainer.getSubContainer(
                        cms,
                        cms.getRequestContext().getLocale());

                    // adding all sub-items to the elements data
                    for (CmsContainerElementBean subElement : subContainer.getElements()) {
                        CmsContainerElementData subItemData = elemUtil.getElementData(subElement, containers);
                        result.add(subItemData);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the map a resource type to be newly created for this container-page.<p>
     * 
     * @param cms the current cms object
     * @param request the current request
     * 
     * @return the map a resource type to be newly created for this container-page
     * 
     * @throws CmsRpcException if something goes wrong reading the ADE configuration
     */
    private Map<String, String> getNewTypes(CmsObject cms, HttpServletRequest request) throws CmsRpcException {

        Map<String, String> result = new HashMap<String, String>();
        CmsResourceManager resourceManager = OpenCms.getResourceManager();
        try {
            Collection<CmsResource> resources = OpenCms.getADEManager().getCreatableElements(
                cms,
                cms.getRequestContext().getUri(),
                request);
            for (CmsResource resource : resources) {
                result.put(
                    resourceManager.getResourceType(resource).getTypeName(),
                    resource.getStructureId().toString());
            }
        } catch (CmsException e) {
            error(e);
        }
        return result;
    }

    /**
     * Returns the no-edit reason for the given resource.<p>
     * 
     * @param cms the current cms object
     * @param containerPage the resource
     * 
     * @return the no-edit reason, empty if editing is allowed
     * 
     * @throws CmsException is something goes wrong
     */
    private String getNoEditReason(CmsObject cms, CmsResource containerPage) throws CmsException {

        return new CmsResourceUtil(cms, containerPage).getNoEditReason(OpenCms.getWorkplaceManager().getWorkplaceLocale(
            cms));
    }

    /**
     * Returns the session cache.<p>
     * 
     * @return the session cache
     */
    private CmsADESessionCache getSessionCache() {

        if (m_sessionCache == null) {
            m_sessionCache = (CmsADESessionCache)getRequest().getSession().getAttribute(
                CmsADESessionCache.SESSION_ATTR_ADE_CACHE);
            if (m_sessionCache == null) {
                m_sessionCache = new CmsADESessionCache(getCmsObject());
                getRequest().getSession().setAttribute(CmsADESessionCache.SESSION_ATTR_ADE_CACHE, m_sessionCache);
            }
        }
        return m_sessionCache;
    }

    /**
     * Helper method for converting a CmsSubContainer to a CmsSubContainerBean when saving a sub container.<p>
     * 
     * @param subContainer the sub-container data
     * @param containerpageUri the URI of the container page 
     * 
     * @return the sub-container bean
     */
    private CmsSubContainerBean getSubContainerBean(CmsSubContainer subContainer, String containerpageUri) {

        CmsObject cms = getCmsObject();
        CmsADESessionCache cache = getSessionCache();
        List<CmsContainerElementBean> elements = new ArrayList<CmsContainerElementBean>();
        for (CmsContainerElement elementData : subContainer.getElements()) {
            try {
                if (elementData.isNew()) {
                    elementData = createNewElement(
                        containerpageUri,
                        elementData.getClientId(),
                        elementData.getNewType());
                }
                CmsContainerElementBean element = cache.getCacheContainerElement(elementData.getClientId());

                // make sure resource is readable, 
                if (cms.existsResource(element.getElementId())) {
                    elements.add(element);
                }

            } catch (Exception e) {
                log(e.getLocalizedMessage(), e);
            }
        }
        return new CmsSubContainerBean(
            subContainer.getTitle(),
            subContainer.getDescription(),
            elements,
            subContainer.getTypes());
    }
}
