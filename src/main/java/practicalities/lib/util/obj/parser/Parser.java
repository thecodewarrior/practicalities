package practicalities.lib.util.obj.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import practicalities.lib.util.obj.parser.OBJModel.UVsOutOfBoundsException;

public class Parser {
    private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
    private static Set<String> unknownObjectCommands = new HashSet<String>();
    public MaterialLibrary materialLibrary = new MaterialLibrary();
    private IResourceManager manager;
    private InputStreamReader objStream;
    private BufferedReader objReader;
    private ResourceLocation objFrom;

    private List<String> groupList = Lists.newArrayList();
    private List<Vertex> vertices = Lists.newArrayList();
    private List<Normal> normals = Lists.newArrayList();
    private List<TextureCoordinate> texCoords = Lists.newArrayList();

    public Parser(IResource from, IResourceManager manager) throws IOException
    {
        this.manager = manager;
        this.objFrom = from.getResourceLocation();
        this.objStream = new InputStreamReader(from.getInputStream(), Charsets.UTF_8);
        this.objReader = new BufferedReader(objStream);
    }

    public List<String> getElements()
    {
        return this.groupList;
    }

    public OBJModel parse() throws IOException
    {
        String currentLine = "";
        Material material = new Material();
        material.setName(Material.DEFAULT_NAME);
        int usemtlCounter = 0;

//        float[] minUVBounds = new float[] {0.0f, 0.0f};
//        float[] maxUVBounds = new float[] {1.0f, 1.0f};
        EnumVendor vendor = EnumVendor.NONE;
        for (;;)
        {
            currentLine = objReader.readLine();
            if (currentLine == null) break;
            currentLine.trim();
            
            if(currentLine.startsWith("# Blender")) {
            	vendor = EnumVendor.BLENDER;
            }
            
            if (currentLine.isEmpty() || currentLine.startsWith("#")) continue;

            String[] fields = WHITE_SPACE.split(currentLine, 2);
            String key = fields[0];
            String data = fields[1];
            String[] splitData = WHITE_SPACE.split(data);

            if (key.equalsIgnoreCase("mtllib"))
                this.materialLibrary.parseMaterials(manager, data, objFrom);
            else if (key.equalsIgnoreCase("usemtl"))
            {
                material = this.materialLibrary.materials.get(data);
                usemtlCounter++;
            }
            else if (key.equalsIgnoreCase("v"))
            {
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++)
                    floatSplitData[i] = Float.parseFloat(splitData[i]);
                Vector4f pos = new Vector4f(floatSplitData[0], floatSplitData[1], floatSplitData[2], floatSplitData.length == 4 ? floatSplitData[3] : 1);
                Vertex vertex = new Vertex(pos, material);
                this.vertices.add(vertex);
            }
            else if (key.equalsIgnoreCase("vn"))
            {
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++)
                    floatSplitData[i] = Float.parseFloat(splitData[i]);
                Normal normal = new Normal(floatSplitData);
                this.normals.add(normal);
            }
            else if (key.equalsIgnoreCase("vt"))
            {
                float[] floatSplitData = new float[splitData.length];
                for (int i = 0; i < splitData.length; i++)
                    floatSplitData[i] = Float.parseFloat(splitData[i]);
                TextureCoordinate texCoord = new TextureCoordinate(new Vector3f(floatSplitData[0], floatSplitData[1], floatSplitData.length == 3 ? floatSplitData[2] : 1));
                if(vendor == EnumVendor.BLENDER) {
                	texCoord.v = 1-texCoord.v;
                }
                if (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f)
                    throw new UVsOutOfBoundsException(this.objFrom);
//                this.UVsOutOfBounds = (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f);

//                if (texCoord.u < 0.0f || texCoord.u > 1.0f || texCoord.v < 0.0f || texCoord.v > 1.0f)
//                {
//                    this.UVsOutOfBounds = true;
//                    texCoord.u -= Math.floor(texCoord.u);
//                    texCoord.v -= Math.floor(texCoord.v);
//                }

//                minUVBounds[0] = floatSplitData[0] < minUVBounds[0] ? floatSplitData[0] : minUVBounds[0];
//                minUVBounds[1] = floatSplitData[1] < minUVBounds[1] ? floatSplitData[1] : minUVBounds[1];
//                maxUVBounds[0] = floatSplitData[0] > maxUVBounds[0] ? floatSplitData[0] : maxUVBounds[0];
//                maxUVBounds[1] = floatSplitData[1] > maxUVBounds[1] ? floatSplitData[1] : maxUVBounds[1];
//                FMLLog.info("u: [%f, %f] v: [%f, %f]", minUVBounds[]);
                this.texCoords.add(texCoord);
            }
            else if (key.equalsIgnoreCase("f"))
            {
                String[][] splitSlash = new String[splitData.length][];
                if (splitData.length > 4) FMLLog.warning("OBJModel.Parser: found a face ('f') with more than 4 vertices, only the first 4 of these vertices will be rendered!");

                int vert = 0;
                int texCoord = 0;
                int norm = 0;

                List<Vertex> v = Lists.newArrayListWithCapacity(splitData.length);
//                List<TextureCoordinate> t = Lists.newArrayListWithCapacity(splitData.length);
//                List<Normal> n = Lists.newArrayListWithCapacity(splitData.length);

                for (int i = 0; i < splitData.length; i++)
                {
                    if (splitData[i].contains("//"))
                    {
                        splitSlash[i] = splitData[i].split("//");

                        vert = Integer.parseInt(splitSlash[i][0]);
                        vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                        norm = Integer.parseInt(splitSlash[i][1]);
                        norm = norm < 0 ? this.normals.size() - 1 : norm - 1;

                        Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                        newV.setNormal(this.normals.get(norm));

                        v.add(newV);
//                        n.add(this.normals.get(norm));
                    }
                    else if (splitData[i].contains("/"))
                    {
                        splitSlash[i] = splitData[i].split("/");

                        vert = Integer.parseInt(splitSlash[i][0]);
                        vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;
                        texCoord = Integer.parseInt(splitSlash[i][1]);
                        texCoord = texCoord < 0 ? this.texCoords.size() - 1 : texCoord - 1;
                        if (splitSlash[i].length > 2)
                        {
                            norm = Integer.parseInt(splitSlash[i][2]);
                            norm = norm < 0 ? this.normals.size() - 1 : norm - 1;
                        }

                        Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                        newV.setTextureCoordinate(this.texCoords.get(texCoord));
                        newV.setNormal(splitSlash[i].length > 2 ? this.normals.get(norm) : null);

                        v.add(newV);
//                        t.add(this.texCoords.get(texCoord));
//                        if (splitSlash[i].length > 2) n.add(this.normals.get(norm));
                    }
                    else
                    {
                        splitSlash[i] = splitData[i].split("");

                        vert = Integer.parseInt(splitData[i]);
                        vert = vert < 0 ? this.vertices.size() - 1 : vert - 1;

                        Vertex newV = new Vertex(new Vector4f(this.vertices.get(vert).getPos()), this.vertices.get(vert).getMaterial());
                        v.add(newV);
                    }
                }
                
                Vertex[] va = new Vertex[v.size()];
                
                if(vendor == EnumVendor.BLENDER) {
                	// BUGFIX: Blender exports it's +Y axis to the -Z axis.
	                for (Vertex vertex : v) {
	                	vertex.position.z = -vertex.position.z;
					}
	                Lists.reverse(v).toArray(va); // vertices are switched CW <=> CCW when we reverse the z axis
                } else {
                	v.toArray(va);
                }
                
//                TextureCoordinate[] ta = new TextureCoordinate[t.size()];
//                t.toArray(ta);
//                Normal[] na = new Normal[n.size()];
//                n.toArray(na);
                Face face = new Face(va, material.name);
                if (usemtlCounter < this.vertices.size())
                {
                    for (Vertex ver : face.getVertices())
                    {
                        ver.setMaterial(material);
                    }
                }

                if (groupList.isEmpty())
                {
                    if (this.materialLibrary.getGroups().containsKey(Group.DEFAULT_NAME))
                    {
                        this.materialLibrary.getGroups().get(Group.DEFAULT_NAME).addFace(face);
                    }
                    else
                    {
                        Group def = new Group(Group.DEFAULT_NAME, null);
                        def.addFace(face);
                        this.materialLibrary.getGroups().put(Group.DEFAULT_NAME, def);
                    }
                }
                else
                {
                    for (String s : groupList)
                    {
                        if (this.materialLibrary.getGroups().containsKey(s))
                        {
                            this.materialLibrary.getGroups().get(s).addFace(face);
                        }
                        else
                        {
                            Group e = new Group(s, null);
                            e.addFace(face);
                            this.materialLibrary.getGroups().put(s, e);
                        }
                    }
                }
            }
            else if (key.equalsIgnoreCase("g") || key.equalsIgnoreCase("o"))
            {
                groupList.clear();
                if (key.equalsIgnoreCase("g"))
                {
                    String[] splitSpace = data.split(" ");
                    for (String s : splitSpace)
                        groupList.add(s);
                }
                else
                {
                    groupList.add(data);
                }
            }
            else
            {
                if (!unknownObjectCommands.contains(key))
                {
                    unknownObjectCommands.add(key);
                    FMLLog.info("OBJLoader.Parser: command '%s' (model: '%s') is not currently supported, skipping", key, objFrom);
                }
            }
        }

        OBJModel model = new OBJModel(this.materialLibrary, this.objFrom);
        model.vendor = vendor;
//        model.getMatLib().setUVBounds(minUVBounds[0], maxUVBounds[0], minUVBounds[1], maxUVBounds[1]);
        return model;
    }
}
