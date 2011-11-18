uniform sampler2D u_tex0;
uniform float u_opacity;
uniform int u_type;

varying vec4 v_texCoord;

void main()
{
    //Circle centered on origin
    float dist = u_type == 0 ? v_texCoord.x : length(v_texCoord.xy);
	vec4 col = texture(u_tex0, vec2(dist, 0));
    gl_FragColor = vec4(col.r, col.g, col.b, col.a * u_opacity);
}