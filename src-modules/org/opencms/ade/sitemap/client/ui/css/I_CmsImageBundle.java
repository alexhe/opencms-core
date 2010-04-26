/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/sitemap/client/ui/css/Attic/I_CmsImageBundle.java,v $
 * Date   : $Date: 2010/04/26 13:39:53 $
 * Version: $Revision: 1.3 $
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

package org.opencms.ade.sitemap.client.ui.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resource bundle to access CSS and image resources.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3 $
 * 
 * @since 8.0.0
 */
public interface I_CmsImageBundle extends ClientBundle {

    /** The button CSS. */
    public interface I_CmsButtonCss extends CssResource {

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarDelete();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarEdit();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarGoto();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarMove();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarNew();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarParent();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String hoverbarSubsitemap();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String toolbarRedo();

        /** 
         * Access method.<p>
         * 
         * @return the CSS class name
         */
        String toolbarUndo();
    }

    /** The bundle instance. */
    I_CmsImageBundle INSTANCE = GWT.create(I_CmsImageBundle.class);

    /**
     * Access method.<p>
     * 
     * @return the button CSS
     */
    @Source("button.css")
    I_CmsButtonCss buttonCss();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/delete.png")
    ImageResource hoverbarDelete();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/delete_disabled.png")
    ImageResource hoverbarDeleteDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/edit.png")
    ImageResource hoverbarEdit();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/edit_disabled.png")
    ImageResource hoverbarEditDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/goto.png")
    ImageResource hoverbarGoto();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/goto_disabled.png")
    ImageResource hoverbarGotoDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/move.png")
    ImageResource hoverbarMove();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/move_disabled.png")
    ImageResource hoverbarMoveDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/new.png")
    ImageResource hoverbarNew();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/new_disabled.png")
    ImageResource hoverbarNewDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/parent.png")
    ImageResource hoverbarParent();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/parent_disabled.png")
    ImageResource hoverbarParentDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/subsitemap.png")
    ImageResource hoverbarSubsitemap();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/subsitemap_disabled.png")
    ImageResource hoverbarSubsitemapDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/redo.png")
    ImageResource toolbarRedo();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/redo_disabled.png")
    ImageResource toolbarRedoDisabled();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/undo.png")
    ImageResource toolbarUndo();

    /** 
     * Access method.<p>
     * 
     * @return an image resource
     */
    @Source("images/undo_disabled.png")
    ImageResource toolbarUndoDisabled();
}
