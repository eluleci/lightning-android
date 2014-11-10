package com.android.moment.moment.net.model.component;

import android.util.SparseArray;

import java.io.Serializable;

/**
 * ResourcePath represents a Resource with optional ID and a Version-number. A ResourcePath can have
 * a parent-ResourcePath.
 * ResourcePath is immutable except Version number which can be incremented only.
 */
public class ResourcePath implements Serializable {

    private static final String TAG = "ResourcePath";
    private String id = "";
    private final Resource resourceType;
    private ResourcePath parentResource;
    private int version = 0;
    private static SparseArray<ResourcePath> resourceRoots = new SparseArray<ResourcePath>();

    /**
     * Constructs a ResourcePath with a certain id and a certain Resourcetype
     *
     * @param id           the id of the ResourcePath
     * @param resourceType the type of Resource of this ResourcePath
     */
    public ResourcePath(String id, Resource resourceType) {
        this.id = id;
        this.resourceType = resourceType;
    }

    /**
     * Constructs a 1st level ResourcePath of a certain resourceType.
     * It is recommended to use the static method ResourcePath.resourceRoot(Resource type) instead.
     *
     * @param resourceType the type of Resource of this ResourcePath
     */
    public ResourcePath(Resource resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Constructs a ResourcePath of a plain certain ResourceType
     *
     * @param resourceType   the type of Resource of this ResourcePath
     * @param parentResource the parent of this ResourcePath
     */
    public ResourcePath(Resource resourceType, ResourcePath parentResource) {
        this.resourceType = resourceType;
        this.parentResource = parentResource;
    }

    /**
     * Constructs a ResourcePath with a certain ID, type of Resource and a parent ResourcePath
     *
     * @param id             the id of the ResourcePath
     * @param resourceType   the type of Resource of this ResourcePath
     * @param parentResource the parent of this ResourcePath
     */
    public ResourcePath(String id, Resource resourceType, ResourcePath parentResource) {
        this.id = id;
        this.resourceType = resourceType;
        this.parentResource = parentResource;
    }

    /**
     * @return the id of this
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type of Resource of this
     */
    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * @return the version number of this (also called "revision number")
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * returns the top parent of this resource path. If it is a 1st-level resource path (root) it returns this.
     *
     * @return ResourcePath object that represents the top of the path
     */
    public ResourcePath getTopParent() {
        if (this.parentResource == null) {
            return this;
        } else {
            return parentResource.getTopParent();
        }
    }

    /**
     * @return true if ResourcePath has parent-ResourcePath
     */
    public boolean hasParent() {
        return this.parentResource != null;
    }

    /**
     * @return the ResourcePath that is the direct parent of this.
     */
    public ResourcePath getParent() {
        return this.parentResource;
    }

    /**
     * Returns a singleton ResourcePath object representing the root of the resource, such as "boards" or "profiles"
     *
     * @param resource the Ressource to generate the Path from
     * @return the root RessourcePath for parameter resource
     */
    public static ResourcePath resourceRoot(Resource resource) {
        ResourcePath result = resourceRoots.get(resource.ordinal());
        if (result == null) {
            result = new ResourcePath(resource);
            resourceRoots.put(resource.ordinal(), result);
        }
        return result;
    }

    /**
     * Parses a String and generates a ResourcePath from it.
     *
     * @param resourcePathString the string representing a ResourcePath
     * @return the ResourcePath object containing information parsed from String, null if resourcePath was not correct
     */
    public static ResourcePath generateResourcePath(String resourcePathString) {
        try {
            return parseFromString(resourcePathString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input string is not a valid ResourcePath: " + resourcePathString, e);
        }
    }

    private static ResourcePath parseFromString(String resourcePathString) {
        if (resourcePathString.equals("")) {
            return null;
        }

        int indexOfSlash = resourcePathString.indexOf("/");
        String parentPath;
        String childPath;

        if (indexOfSlash != -1) {
            parentPath = resourcePathString.substring(0, indexOfSlash);

            // creating parent res
            int length = parentPath.length();
            int indexOfDot = parentPath.indexOf(".");
            int indexOfAt = parentPath.indexOf("@");

            Resource resType = Resource.parseResource(parentPath);
            String id;
            int ver = -1;
            if (indexOfAt == -1) {
                id = parentPath.substring(indexOfDot + 1, length);
            } else {
                id = parentPath.substring(indexOfDot + 1, indexOfAt);
                ver = Integer.parseInt(parentPath.substring(indexOfAt + 1, length));
            }

            ResourcePath parentResourcePath;

            parentResourcePath = new ResourcePath(id, resType);
            parentResourcePath.setVersion(ver);

            // getting child res
            childPath = resourcePathString.substring(indexOfSlash + 1, resourcePathString.length());
            ResourcePath childResourcePath = parseFromString(childPath);
            childResourcePath.getTopParent().setParentResourcePath(parentResourcePath);

            return childResourcePath;
        } else {
            // creating res path
            childPath = resourcePathString;

            int length = childPath.length();
            int indexOfDot = childPath.indexOf(".");
            int indexOfAt = childPath.indexOf("@");

            Resource resType = Resource.parseResource(childPath);
            String id;
            int ver = -1;
            ResourcePath resourcePath;

            try {
                if (indexOfAt > indexOfDot) {
                    id = childPath.substring(indexOfDot + 1, indexOfAt);
                    ver = Integer.parseInt(childPath.substring(indexOfAt + 1, length));
                } else {
                    id = childPath.substring(indexOfDot + 1, childPath.length());
                }

                resourcePath = new ResourcePath(id, resType);
                resourcePath.setVersion(ver);
            } catch (NumberFormatException e) {
                String resource = childPath.substring(indexOfDot + 1, childPath.length());
                Resource resourceR = Resource.parseResource(resource);
                resourcePath = new ResourcePath(resourceR);

            }
            return resourcePath;
        }
    }

    private void setParentResourcePath(ResourcePath parentResource) {
        this.parentResource = parentResource;
    }

    /**
     * Two resourcePath are equal when they point to the same object.
     * The version number can be different.
     *
     * @param another the other ResourcePath
     * @return true if both resourcePaths point to same object
     */
    @Override
    public boolean equals(Object another) {
        if (another == null || !(another instanceof ResourcePath)) {
            return false;
        }
        ResourcePath other = (ResourcePath) another;
        boolean result = this.getId() == other.getId()
                && this.resourceType == other.resourceType;
        boolean parentResult = false;
        if (other.parentResource != null && this.parentResource != null) {
            parentResult = parentResource.equals(other.parentResource);
        } else if (other.parentResource == null && parentResource == null) {
            parentResult = true;
        }
        return result && parentResult;
    }

    /**
     * sets the Version number of the model object.
     *
     * @param version the version Number to be set
     * @return true, if version number is set (current version < previous)
     */
    public boolean setVersion(int version) {
        if (version != 0 && this.getId() == "") {
            return true;
        }
        if (this.version < version) {
            this.version = version;
            return true;
        } else {
            return false;
        }
    }

    public enum Resource {
        PROFILES("profiles"), BOARDS("boards"), CONVERSATIONS("conversations"),
        FOLLOWERS("followers"), ENTRIES("entries"), CATEGORIES("categories"),
        NONE("none"), REACTIONS("reactions"), NEWS_FEED("_newsfeed"),
        SEARCH("search"), NOTIFICATIONS("notifications"), PRESENCES("presences");

        /**
         * @param type
         */
        private Resource(final String type) {
            this.type = type;
        }

        /**
         * parses the Resource from a String representing the type
         *
         * @param resourceType the Resource as String
         * @return the Resource object if found, null otherwise
         */
        public static Resource parseResource(String resourceType) {
            for (Resource r : Resource.values()) {
                if (resourceType.contains(r.toString())) {
                    return r;
                }
            }
            throw new IllegalArgumentException("resourcetype not found");
        }

        private final String type;

        @Override
        public String toString() {
            return type;
        }
    }

    @Override
    public int hashCode() {
        //the hashCode should be same for 2 objects representing same ressource, the String representation will always be the pure resource-path as string
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder resBuilder = new StringBuilder();
        if (parentResource != null) {
            resBuilder.append(parentResource.toString()).append("/");
        }

        resBuilder.append(resourceType);

        if (id != "") {
            resBuilder.append(".").append(id);
        }
        return resBuilder.toString();
    }

}
