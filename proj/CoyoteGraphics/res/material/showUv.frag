varying vec4 v_texCoord;

void main()
{
	gl_FragColor = vec4(v_texCoord.xy, 0, 1);
}