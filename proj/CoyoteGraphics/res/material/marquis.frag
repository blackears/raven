uniform vec4 u_colorFg;
uniform vec4 u_colorBg;
uniform float u_lineWidth;
uniform float u_offset;

varying vec4 v_pos;

void main()
{
    float grad = fract((v_pos.x + v_pos.y + u_offset) / u_lineWidth);

	gl_FragColor = (grad > .5) ? u_colorFg : u_colorBg;
}
