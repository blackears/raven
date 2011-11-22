uniform sampler2D u_tex0;
uniform float u_opacity;

varying vec4 v_texCoord;

void main()
{
//	gl_FragColor = v_texCoord + vec4(1, 0, 0, 1);
//	gl_FragColor = vec4(1, 0, 0, 1);

	vec4 col = texture2D(u_tex0, v_texCoord.xy);
    gl_FragColor = vec4(col.r, col.g, col.b, col.a * u_opacity);
}